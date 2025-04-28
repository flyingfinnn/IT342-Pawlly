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
} from "@mui/material";
import { Visibility, VisibilityOff, Close } from "@mui/icons-material";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import logo from "../assets/logo_colored.png";
import authBg from "../assets/auth_bg.png";
import { useUser } from '../components/UserContext';


const AuthModal = ({ open, handleClose }) => {
    const [showPassword, setShowPassword] = useState(false);
    const [isLogin, setIsLogin] = useState(true); // true for Login, false for Signup
    const navigate = useNavigate();
    const { updateUser } = useUser();

    const [usernameExists, setUsernameExists] = useState(false);

    const [emailExists, setEmailExists] = useState(false);




    const toggleShowPassword = () => setShowPassword(!showPassword);

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
                "http://localhost:8080/api/auth/login",
                loginCredentials,
                { headers: { "Content-Type": "application/json" } }
            );
            localStorage.setItem("token", response.data);
            // Fetch the user details
            const userResponse = await axios.get("http://localhost:8080/api/users/me", {
                headers: { Authorization: `Bearer ${response.data}` },
            });

            // Store the first name in local storage
            // localStorage.setItem("firstName", userResponse.data.firstName);

            updateUser(userResponse.data);

            alert("Logged in successfully");
            navigate("/profile");
            handleClose();
        } catch (error) {
            alert("Invalid username or password.");
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

        // If the username field is being updated, check its availability
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
            alert("Password must be at least 8 characters long.");
            return;
        }
        try {
            await axios.post("http://localhost:8080/api/auth/signup", signupCredentials);
            alert("Signup successful!");
            setIsLogin(true);
            handleClose();
        } catch (error) {
            alert("Error signing up");
            console.error("Error during signup:", error);
        }
    };

    const checkUsername = async (username) => {
        try {
            const response = await axios.get(
                `http://localhost:8080/api/auth/check-username?username=${username}`
            );
            setUsernameExists(response.data); // Response will be true if username exists
        } catch (error) {
            console.error("Error checking username:", error);
        }
    };

    const checkEmail = async (email) => {
        try {
            const response = await axios.get(
                `http://localhost:8080/api/auth/check-email?email=${email}`
            );
            setEmailExists(response.data); // Response will be true if email exists
        } catch (error) {
            console.error("Error checking email:", error);
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            fullWidth
            maxWidth="sm"
            sx={{
                "& .MuiBackdrop-root": {
                    backgroundColor: "rgba(0, 0, 0, 0.5)",
                    backdropFilter: "blur(8px)",
                },
                "& .MuiPaper-root": {
                    width: "90vw",
                    maxWidth: "600px",
                    height: "650px", // Set consistent height for both forms
                    borderRadius: 2,
                },
            }}
        >
            <Grid container component={Paper} elevation={3} sx={{ height: "100%" }}>
                {/* Background Section */}
                <Grid
                    item
                    xs={12} md={4}
                    sx={{
                        display: { xs: "none", md: "block" },
                        backgroundImage: `url(${authBg})`,
                        backgroundSize: "cover",
                        backgroundPosition: "center",
                    }}
                />

                {/* Form Section */}
                <Grid
                    item
                    xs={12} md={8}
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        position: "relative",
                        alignItems: "center",
                        justifyContent: "center",
                        padding: { xs: 2, md: 4 },
                        height: "100%", // Ensure the form section takes full height
                    }}
                >
                    <IconButton
                        onClick={handleClose}
                        sx={{
                            position: "absolute",
                            top: 16,
                            right: 16,
                            color: "grey.500",
                        }}
                    >
                        <Close />
                    </IconButton>

                    <Box sx={{ width: '100%', textAlign: 'center' }}>
                        {isLogin ? (
                            <>
                                <Box mb={2}>
                                    <img src={logo} alt="Logo" style={{ maxWidth: "60%" }} />
                                </Box>
                                <Typography variant="h5" sx={{ mb: 3 }}>
                                    Login
                                </Typography>

                                <form onSubmit={handleLoginSubmit} style={{ width: '100%' }}>
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
                                    </Grid>
                                    <Typography
                                        variant="body2"
                                        sx={{ mt: 2 }}
                                    >
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
                                <Typography variant="h5" sx={{ mb: 3, textAlign: 'center' }}>
                                    Signup
                                </Typography>

                                <form onSubmit={handleSignupSubmit} style={{ width: '100%' }}>
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
                                                error={usernameExists} // Show red border if username exists
                                                helperText={usernameExists ? "Username is already taken" : ""} // Dynamic helper text
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

                                                    // Check email availability
                                                    checkEmail(email);
                                                }}
                                                error={emailExists} // Display error if email exists
                                                helperText={emailExists ? "Email is already registered" : ""} // Show message if registered
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
                                                signupCredentials.password.length > 0 && signupCredentials.password.length < 8
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
                                                disabled={usernameExists || emailExists} // Disable if username or email exists
                                            >
                                                Signup
                                            </Button>
                                        </Grid>
                                    </Grid>
                                    <Typography
                                        variant="body2"
                                        sx={{ mt: 2 }}
                                    >
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
    );
};

export default AuthModal;