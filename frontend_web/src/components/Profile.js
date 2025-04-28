import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import {
    Container,
    Box,
    Typography,
    Button,
    TextField,
    Avatar,
    Grid,
    Stack,
    InputAdornment,
    IconButton,
    Tooltip,
    Snackbar,
    Alert,
} from "@mui/material";
import { useUser } from "./UserContext";
import { Visibility, VisibilityOff } from "@mui/icons-material";

const Profile = () => {
    const { user, updateUser, loading } = useUser();
    const [editingUserId, setEditingUserId] = useState(null);
    const [editFormData, setEditFormData] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",
        address: "",
        phoneNumber: "",
    });
    const [profilePicture, setProfilePicture] = useState(null);
    const [passwordChange, setPasswordChange] = useState(false);
    const [passwordData, setPasswordData] = useState({
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
    });

    const [showOldPassword, setShowOldPassword] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [usernameExists, setUsernameExists] = useState(false);
    const [emailExists, setEmailExists] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success",
    });
    const fileInputRef = useRef(null);

    useEffect(() => {
        if (user) {
            setEditFormData({
                firstName: user.firstName,
                lastName: user.lastName,
                username: user.username,
                email: user.email,
                address: user.address,
                phoneNumber: user.phoneNumber,
            });
            setProfilePicture(user.profilePicture);
        }
    }, [user]);

    const handleSnackbarClose = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    const handleEdit = () => setEditingUserId(user?.userId);

    const handleCancelEdit = () => {
        setEditingUserId(null);
        setUsernameExists(false);
        setEmailExists(false);
    };

    const handleEditChange = async (e) => {
        const { name, value } = e.target;
        setEditFormData({ ...editFormData, [name]: value });

        if (name === "username" && value.trim() !== "") {
            checkUsername(value.trim());
        }
        if (name === "email" && value.trim() !== "") {
            checkEmail(value.trim());
        }
    };

    const checkUsername = async (username) => {
        try {
            if (username !== user.username) {
                const response = await axios.get(
                    `http://localhost:8080/api/auth/check-username?username=${username}`
                );
                setUsernameExists(response.data);
            } else {
                setUsernameExists(false);
            }
        } catch (error) {
            console.error("Error checking username:", error);
        }
    };

    const checkEmail = async (email) => {
        try {
            if (email !== user.email) {
                const response = await axios.get(
                    `http://localhost:8080/api/auth/check-email?email=${email}`
                );
                setEmailExists(response.data);
            } else {
                setEmailExists(false);
            }
        } catch (error) {
            console.error("Error checking email:", error);
        }
    };

    const handleSaveEdit = async () => {
        if (usernameExists || emailExists) {
            setSnackbar({
                open: true,
                message: "Please resolve the validation errors before saving.",
                severity: "warning",
            });
            return;
        }

        setIsSaving(true);
        const token = localStorage.getItem("token");
        if (!token) {
            setSnackbar({
                open: true,
                message: "Token is missing. Please log in again.",
                severity: "error",
            });
            return;
        }

        const formData = new FormData();
        formData.append("user", JSON.stringify(editFormData));
        if (profilePicture) {
            formData.append("profilePicture", profilePicture);
        }

        try {
            const response = await axios.put(
                `http://localhost:8080/api/users/${user.userId}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            if (response.status === 200) {
                const { updatedUser, newToken } = response.data;

                if (newToken) {
                    localStorage.setItem("token", newToken);
                }

                const updatedResponse = await axios.get(
                    `http://localhost:8080/api/users/me`,
                    {
                        headers: { Authorization: `Bearer ${newToken || token}` },
                    }
                );

                if (updatedResponse.status === 200) {
                    updateUser(updatedResponse.data);
                    setProfilePicture(updatedResponse.data.profilePicture);
                    setEditingUserId(null);
                    setSnackbar({
                        open: true,
                        message: "Profile updated successfully!",
                        severity: "success",
                    });
                }
            } else {
                setSnackbar({
                    open: true,
                    message: "Failed to update profile.",
                    severity: "error",
                });
            }
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Error saving profile changes. Please try again.",
                severity: "error",
            });
            console.error("Error saving user:", error);
        } finally {
            setIsSaving(false);
        }
    };

    const handlePasswordChange = async () => {
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setSnackbar({
                open: true,
                message: "New passwords do not match!",
                severity: "warning",
            });
            return;
        }
        const token = localStorage.getItem("token");
        if (!token) {
            setSnackbar({
                open: true,
                message: "Token is missing. Please log in again.",
                severity: "error",
            });
            return;
        }
        try {
            const response = await axios.post(
                `http://localhost:8080/api/users/change-password`,
                passwordData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );
            if (response.status === 200) {
                setSnackbar({
                    open: true,
                    message: "Password changed successfully!",
                    severity: "success",
                });
                setPasswordChange(false);
                setPasswordData({ oldPassword: "", newPassword: "", confirmPassword: "" });
            } else {
                setSnackbar({
                    open: true,
                    message: "Failed to change password.",
                    severity: "error",
                });
            }
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Error changing password. Please try again.",
                severity: "error",
            });
            console.error("Error changing password:", error);
        }
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user) {
        return <div>User not found. Please log in again.</div>;
    }

    return (
        <Container>
            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbar.severity} sx={{ width: "100%" }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
            <Box sx={{ mt: 5, maxWidth: 500, mx: "auto" }}>
                <Typography variant="h4" align="center" gutterBottom>
                    Profile
                </Typography>
                <Box
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                        p: 3,
                        borderRadius: 2,
                        boxShadow: 3,
                        bgcolor: "background.paper",
                    }}
                >
                    <Tooltip
                        title={
                            editingUserId
                                ? "Click to upload a new profile picture"
                                : ""
                        }
                    >
                        <Avatar
                            src={
                                profilePicture instanceof File || profilePicture instanceof Blob
                                    ? URL.createObjectURL(profilePicture)
                                    : user.profilePicture
                                        ? `data:image/jpeg;base64,${user.profilePicture}`
                                        : null
                            }
                            alt="Profile Picture"
                            sx={{
                                width: 125,
                                height: 125,
                                bgcolor: "grey.300",
                                mb: 2,
                                fontSize: "48px",
                                cursor: editingUserId ? "pointer" : "default",
                            }}
                            onClick={() => editingUserId && fileInputRef.current.click()}
                        >
                            {(!profilePicture && !user.profilePicture) &&
                                (user.firstName ? user.firstName[0] : "")}
                        </Avatar>
                    </Tooltip>
                    <input
                        type="file"
                        accept="image/*"
                        ref={fileInputRef}
                        onChange={(e) => setProfilePicture(e.target.files[0])}
                        style={{ display: "none" }}
                    />
                    <Stack spacing={2} sx={{ width: "100%" }}>
                        <Grid container >
                            <Grid item xs={6}
                                  sx={{ pl: 0, pr: 2, pt: 2 }}
                            >
                                <TextField
                                    name="firstName"
                                    label="First Name"
                                    value={editFormData.firstName}
                                    onChange={handleEditChange}
                                    variant="outlined"
                                    fullWidth
                                    size="small"
                                    InputProps={{
                                        readOnly: !editingUserId,
                                    }}
                                />
                            </Grid>
                            <Grid item xs={6}
                                  sx={{ pl: 0, pr: 0, pt: 2 }}
                            >
                                <TextField
                                    name="lastName"
                                    label="Last Name"
                                    value={editFormData.lastName}
                                    onChange={handleEditChange}
                                    variant="outlined"
                                    fullWidth
                                    size="small"
                                    InputProps={{
                                        readOnly: !editingUserId,
                                    }}
                                />
                            </Grid>
                        </Grid>
                        <TextField
                            name="username"
                            label="Username"
                            value={editFormData.username}
                            onChange={handleEditChange}
                            variant="outlined"
                            fullWidth
                            size="small"
                            error={usernameExists}
                            helperText={usernameExists ? "Username is already taken." : ""}
                        />
                        <TextField
                            name="email"
                            label="Email"
                            value={editFormData.email}
                            onChange={handleEditChange}
                            variant="outlined"
                            fullWidth
                            size="small"
                            error={emailExists}
                            helperText={emailExists ? "Email is already registered." : ""}
                        />

                        <TextField
                            name="address"
                            label="Address"
                            value={editFormData.address}
                            onChange={handleEditChange}
                            variant="outlined"
                            fullWidth
                            size="small"
                            InputProps={{
                                readOnly: !editingUserId,
                            }}
                        />
                        <TextField
                            name="phoneNumber"
                            label="Phone Number"
                            value={editFormData.phoneNumber}
                            onChange={handleEditChange}
                            variant="outlined"
                            fullWidth
                            size="small"
                            InputProps={{
                                readOnly: !editingUserId,
                            }}
                        />
                    </Stack>
                    <Box sx={{ mt: 3, display: "flex", gap: 2 }}>
                        {editingUserId ? (
                            <>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleSaveEdit}
                                    disabled={isSaving || usernameExists || emailExists}
                                >
                                    {isSaving ? "Saving..." : "Save"}
                                </Button>
                                <Button
                                    variant="outlined"
                                    color="secondary"
                                    onClick={handleCancelEdit}
                                >
                                    Cancel
                                </Button>
                            </>
                        ) : (
                            <>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleEdit}
                                >
                                    Edit Profile
                                </Button>
                                <Button
                                    variant="outlined"
                                    color="primary"
                                    onClick={() => setPasswordChange(!passwordChange)}
                                >
                                    Change Password
                                </Button>
                            </>
                        )}
                    </Box>
                    {passwordChange && (
                        <Box sx={{ mt: 3, width: "100%" }}>
                            <TextField
                                label="Old Password"
                                name="oldPassword"
                                type={showOldPassword ? "text" : "password"}
                                value={passwordData.oldPassword}
                                onChange={(e) =>
                                    setPasswordData({
                                        ...passwordData,
                                        oldPassword: e.target.value,
                                    })
                                }
                                fullWidth
                                margin="normal"
                                size="small"
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            <IconButton
                                                onClick={() => setShowOldPassword(!showOldPassword)}
                                            >
                                                {showOldPassword ? <VisibilityOff /> : <Visibility />}
                                            </IconButton>
                                        </InputAdornment>
                                    ),
                                }}
                            />
                            <TextField
                                label="New Password"
                                name="newPassword"
                                type={showNewPassword ? "text" : "password"}
                                value={passwordData.newPassword}
                                onChange={(e) =>
                                    setPasswordData({
                                        ...passwordData,
                                        newPassword: e.target.value,
                                    })
                                }
                                fullWidth
                                margin="normal"
                                size="small"
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            <IconButton
                                                onClick={() => setShowNewPassword(!showNewPassword)}
                                            >
                                                {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                            </IconButton>
                                        </InputAdornment>
                                    ),
                                }}
                            />
                            <TextField
                                label="Confirm New Password"
                                name="confirmPassword"
                                type={showConfirmPassword ? "text" : "password"}
                                value={passwordData.confirmPassword}
                                onChange={(e) =>
                                    setPasswordData({
                                        ...passwordData,
                                        confirmPassword: e.target.value,
                                    })
                                }
                                fullWidth
                                margin="normal"
                                size="small"
                                InputProps={{
                                    endAdornment: (
                                        <InputAdornment position="end">
                                            <IconButton
                                                onClick={() =>
                                                    setShowConfirmPassword(!showConfirmPassword)
                                                }
                                            >
                                                {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                            </IconButton>
                                        </InputAdornment>
                                    ),
                                }}
                            />
                            <Box sx={{ mt: 2, display: "flex", gap: 2 }}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handlePasswordChange}
                                >
                                    Confirm
                                </Button>
                                <Button
                                    variant="outlined"
                                    color="secondary"
                                    onClick={() => setPasswordChange(false)}
                                >
                                    Cancel
                                </Button>
                            </Box>
                        </Box>
                    )}
                </Box>
            </Box>
        </Container>
    );
};

export default Profile;
