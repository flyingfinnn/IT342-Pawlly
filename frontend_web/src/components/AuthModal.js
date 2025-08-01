import React, { useState } from "react";
import {
    Button,
    TextField,
    Box,
    Grid,
    Paper,
    Dialog,
    Typography,
    InputAdornment,
    IconButton,
    Snackbar,
    Alert,
} from "@mui/material";
import { Visibility, VisibilityOff, Close } from "@mui/icons-material";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { createPortal } from "react-dom";
import logo from "../assets/logo_colored.png";
import authBg from "../assets/auth_bg.png";
import { useUser } from "../components/UserContext";
import { msalInstance } from "./msalConfig";

const AuthModal = ({ open, handleClose }) => {
    const [showPassword, setShowPassword] = useState(false);
    const [isLogin, setIsLogin] = useState(true);
    const navigate = useNavigate();
    const { updateUser } = useUser();

    const [usernameExists, setUsernameExists] = useState(false);
    const [emailExists, setEmailExists] = useState(false);

    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success",
    });

    const toggleShowPassword = () => setShowPassword(!showPassword);

    // Microsoft login function
    const handleMicrosoftLogin = async () => {
        try {
            // Ensure MSAL is initialized
            await msalInstance.initialize();
    
            const loginResponse = await msalInstance.loginPopup({
                scopes: ["User.Read"], // Adjust scopes as needed
            });
    
            const tokenResponse = await msalInstance.acquireTokenSilent({
                scopes: ["User.Read"],
                account: loginResponse.account,
            });
    
            // Send the token to the backend
            const response = await axios.post(`${process.env.REACT_APP_BACKEND_URL}/api/auth/microsoft-login`, {
                token: tokenResponse.accessToken, // Ensure this is the correct access token
            });
    
            // Save the JWT in local storage
            localStorage.setItem("token", response.data.token);
    
            // Fetch user details from the backend
            const userResponse = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/users/me`, {
                headers: { Authorization: `Bearer ${response.data.token}` },
            });
    
            // Update the user context
            updateUser(userResponse.data);
    
            // Show success message
            setSnackbar({
                open: true,
                message: `Welcome, ${userResponse.data.firstName}!`,
                severity: "success",
            });
    
            // Close the modal
            handleClose();
        } catch (error) {
            console.error("Microsoft login error:", error);
            setSnackbar({
                open: true,
                message: "Error logging in with Microsoft. Please try again.",
                severity: "error",
            });
        }
    };

    // Login state and handlers
    const [loginCredentials, setLoginCredentials] = useState({
        username: "",
        password: "",
    });

    const handleLoginChange = (e) => {
        setLoginCredentials({
            ...loginCredentials,
            [e.target.name]: e.target.value,
        });
    };

    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                `${process.env.REACT_APP_BACKEND_URL}/api/auth/login`,
                loginCredentials,
                { headers: { "Content-Type": "application/json" } }
            );
            localStorage.setItem("token", response.data);

            const userResponse = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/users/me`, {
                headers: { Authorization: `Bearer ${response.data}` },
            });

            updateUser(userResponse.data);

            setSnackbar({
                open: true,
                message: "Logged in successfully!",
                severity: "success",
            });

            navigate("/");
            handleClose();
            setLoginCredentials({ username: "", password: "" }); // Reset form data
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Invalid username or password.",
                severity: "error",
            });
            console.error("Error during login:", error);
        }
    };

    // Signup state and handlers
    const [signupCredentials, setSignupCredentials] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",
        password: "",
        address: "",
        phoneNumber: "",
    });

    const handleSignupChange = (e) => {
        const { name, value } = e.target;

        setSignupCredentials((prev) => ({
            ...prev,
            [name]: value,
        }));

        if (name === "username" && value.trim() !== "") {
            checkUsername(value.trim());
        }

        if (name === "email" && value.trim() !== "") {
            checkEmail(value.trim());
        }
    };

    const handleSignupSubmit = async (e) => {
        e.preventDefault();

        if (signupCredentials.password.length < 8) {
            setSnackbar({
                open: true,
                message: "Password must be at least 8 characters long.",
                severity: "warning",
            });
            return;
        }
        try {
            await axios.post(`${process.env.REACT_APP_BACKEND_URL}/api/auth/signup`, signupCredentials);
            setSnackbar({
                open: true,
                message: "Signup successful! Please log in.",
                severity: "success",
            });
            setIsLogin(true);
            setSignupCredentials({
                firstName: "",
                lastName: "",
                username: "",
                email: "",
                password: "",
                address: "",
                phoneNumber: "",
            }); // Reset form data
            handleClose();
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Error signing up. Please try again.",
                severity: "error",
            });
            console.error("Error during signup:", error);
        }
    };

    const checkUsername = async (username) => {
        try {
            const response = await axios.get(
                `${process.env.REACT_APP_BACKEND_URL}/api/auth/check-username?username=${username}`
            );
            setUsernameExists(response.data);
        } catch (error) {
            console.error("Error checking username:", error);
        }
    };

    const checkEmail = async (email) => {
        try {
            const response = await axios.get(
                `${process.env.REACT_APP_BACKEND_URL}/api/auth/check-email?email=${email}`
            );
            setEmailExists(response.data);
        } catch (error) {
            console.error("Error checking email:", error);
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    const handleDialogClose = () => {
        handleClose();
        setSignupCredentials({
            firstName: "",
            lastName: "",
            username: "",
            email: "",
            password: "",
            address: "",
            phoneNumber: "",
        });
        setLoginCredentials({ username: "", password: "" });
        setUsernameExists(false);
        setEmailExists(false);
    };

    // Render Snackbar outside the Dialog to ensure visibility
    const snackbarPortal = createPortal(
        <Snackbar
            open={snackbar.open}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
            sx={{
                zIndex: 2000,
            }}
        >
            <Alert
                onClose={handleCloseSnackbar}
                severity={snackbar.severity}
                sx={{ width: "100%" }}
            >
                {snackbar.message}
            </Alert>
        </Snackbar>,
        document.body
    );

    return (
        <>
            {snackbarPortal}

            <Dialog
                open={open}
                onClose={handleDialogClose}
                fullWidth
                maxWidth="sm"
                sx={{
                    "& .MuiBackdrop-root": {
                        backgroundColor: "rgba(0, 0, 0, 0.5)",
                        backdropFilter: "blur(8px)",
                    },
                    "& .MuiPaper-root": {
                        height: "650px",
                        maxHeight: "650px",
                        width: "90vw",
                        maxWidth: "600px",
                        borderRadius: 2,
                    },
                }}
            >
                <Grid container component={Paper} elevation={3} sx={{ height: "100%" }}>
                    <Grid
                        item
                        xs={12}
                        md={4}
                        sx={{
                            display: { xs: "none", md: "block" },
                            backgroundImage: `url(${authBg})`,
                            backgroundSize: "cover",
                            backgroundPosition: "center",
                        }}
                    />

                    <Grid
                        item
                        xs={12}
                        md={8}
                        sx={{
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                            justifyContent: "center",
                            padding: { xs: 2, md: 4 },
                            height: "100%",
                        }}
                    >
                        <IconButton
                            onClick={handleDialogClose}
                            sx={{
                                position: "absolute",
                                top: 16,
                                right: 16,
                                color: "grey.500",
                            }}
                        >
                            <Close />
                        </IconButton>

                        <Box sx={{ width: "100%", textAlign: "center" }}>
                        {isLogin ? (
                            <>
                                <Box mb={2}>
                                    <img src={logo} alt="Logo" style={{ maxWidth: "60%" }} />
                                </Box>
                                <Typography variant="h5" sx={{ mb: 3 }}>
                                    Login
                                </Typography>

                                <form onSubmit={handleLoginSubmit} style={{ width: "100%" }}>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <TextField
                                                label="Username"
                                                name="username"
                                                variant="outlined"
                                                fullWidth
                                                value={loginCredentials.username}
                                                onChange={handleLoginChange}
                                                required
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                label="Password"
                                                name="password"
                                                type={showPassword ? "text" : "password"}
                                                variant="outlined"
                                                fullWidth
                                                value={loginCredentials.password}
                                                onChange={handleLoginChange}
                                                required
                                                InputProps={{
                                                    endAdornment: (
                                                        <InputAdornment position="end">
                                                            <IconButton onClick={toggleShowPassword}>
                                                                {showPassword ? <VisibilityOff /> : <Visibility />}
                                                            </IconButton>
                                                        </InputAdornment>
                                                    ),
                                                }}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Button type="submit" variant="contained" color="primary" fullWidth>
                                                Login
                                            </Button>
                                        </Grid>
                                        <Grid item xs={12}>
                                            {/* Add the Microsoft login button here */}
                                            <Button
                                                variant="contained"
                                                color="secondary"
                                                fullWidth
                                                onClick={handleMicrosoftLogin}
                                            >
                                                Login with Microsoft
                                            </Button>
                                        </Grid>
                                    </Grid>
                                    <Typography variant="body2" sx={{ mt: 2 }}>
                                        Don't have an account?{" "}
                                        <Box
                                            component="span"
                                            onClick={() => setIsLogin(false)}
                                            sx={{ color: "blue", cursor: "pointer", textDecoration: "none" }}
                                        >
                                            Sign up
                                        </Box>
                                        .
                                    </Typography>
                                </form>
                            </>
                            ) : (
                                <>
                                    <Typography variant="h5" sx={{ mb: 3, textAlign: "center" }}>
                                        Signup
                                    </Typography>

                                    <form onSubmit={handleSignupSubmit} style={{ width: "100%" }}>
                                        <Grid container spacing={2}>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    label="First Name"
                                                    name="firstName"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.firstName}
                                                    onChange={handleSignupChange}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12} sm={6}>
                                                <TextField
                                                    label="Last Name"
                                                    name="lastName"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.lastName}
                                                    onChange={handleSignupChange}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    label="Username"
                                                    name="username"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.username}
                                                    onChange={handleSignupChange}
                                                    error={usernameExists}
                                                    helperText={usernameExists ? "Username is already taken" : ""}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    label="Email"
                                                    name="email"
                                                    type="email"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.email}
                                                    onChange={(e) => {
                                                        const email = e.target.value;
                                                        setSignupCredentials({ ...signupCredentials, email });
                                                        checkEmail(email);
                                                    }}
                                                    error={emailExists}
                                                    helperText={emailExists ? "Email is already registered" : ""}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    label="Password"
                                                    name="password"
                                                    type={showPassword ? "text" : "password"}
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.password}
                                                    onChange={(e) => {
                                                        setSignupCredentials({ ...signupCredentials, password: e.target.value });
                                                    }}
                                                    error={signupCredentials.password.length > 0 && signupCredentials.password.length < 8}
                                                    helperText={
                                                        signupCredentials.password.length > 0 &&
                                                        signupCredentials.password.length < 8
                                                            ? "Password must be at least 8 characters long."
                                                            : ""
                                                    }
                                                    required
                                                    InputProps={{
                                                        endAdornment: (
                                                            <InputAdornment position="end">
                                                                <IconButton onClick={toggleShowPassword}>
                                                                    {showPassword ? <VisibilityOff /> : <Visibility />}
                                                                </IconButton>
                                                            </InputAdornment>
                                                        ),
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    label="Address"
                                                    name="address"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.address}
                                                    onChange={handleSignupChange}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <TextField
                                                    label="Phone Number"
                                                    name="phoneNumber"
                                                    variant="outlined"
                                                    fullWidth
                                                    value={signupCredentials.phoneNumber}
                                                    onChange={handleSignupChange}
                                                    required
                                                />
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Button
                                                    type="submit"
                                                    variant="contained"
                                                    color="primary"
                                                    fullWidth
                                                    disabled={usernameExists || emailExists}
                                                >
                                                    Signup
                                                </Button>
                                            </Grid>
                                        </Grid>
                                        <Typography variant="body2" sx={{ mt: 2 }}>
                                            Already have an account?{" "}
                                            <Box
                                                component="span"
                                                onClick={() => setIsLogin(true)}
                                                sx={{ color: "blue", cursor: "pointer", textDecoration: "none" }}
                                            >
                                                Login
                                            </Box>
                                            .
                                        </Typography>
                                    </form>
                                </>
                            )}
                        </Box>
                    </Grid>
                </Grid>
            </Dialog>
        </>
    );
};

export default AuthModal;
