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
    Tabs,
    Tab,
    Card,
    CardContent,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Chip,
    Divider,
} from "@mui/material";
import { useUser } from "./UserContext";
import { Visibility, VisibilityOff, Edit as EditIcon, Save as SaveIcon, Cancel as CancelIcon } from "@mui/icons-material";

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
    const [tabValue, setTabValue] = useState(0);
    const [rehomeApplications, setRehomeApplications] = useState([]);
    const [adoptionApplications, setAdoptionApplications] = useState([]);

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

    useEffect(() => {
        const fetchApplications = async () => {
            if (user) {
                try {
                    // Fetch rehome applications
                    const rehomeResponse = await axios.get(
                        `${process.env.REACT_APP_BACKEND_URL}/api/pet/getAllPets`
                    );
                    const userRehomes = rehomeResponse.data.filter(
                        pet => pet.userName === `${user.firstName} ${user.lastName}`
                    );
                    setRehomeApplications(userRehomes);

                    // Fetch adoption applications
                    const adoptionResponse = await axios.get(
                        `${process.env.REACT_APP_BACKEND_URL}/api/adoptions`
                    );
                    const userAdoptions = adoptionResponse.data.filter(
                        adoption => adoption.name === `${user.firstName} ${user.lastName}`
                    );
                    setAdoptionApplications(userAdoptions);
                } catch (error) {
                    console.error("Error fetching applications:", error);
                }
            }
        };
        fetchApplications();
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
                    `${process.env.REACT_APP_BACKEND_URL}/api/auth/check-username?username=${username}`
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
                    `${process.env.REACT_APP_BACKEND_URL}/api/auth/check-email?email=${email}`
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
                `${process.env.REACT_APP_BACKEND_URL}/api/users/${user.userId}`,
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
                    `${process.env.REACT_APP_BACKEND_URL}/api/users/me`,
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
                `${process.env.REACT_APP_BACKEND_URL}/api/users/change-password`,
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

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING':
            case 'PENDING_REHOME':
                return 'warning';
            case 'APPROVED':
            case 'ACCEPTED_REHOME':
                return 'success';
            case 'REJECTED':
                return 'error';
            default:
                return 'default';
        }
    };

    const renderProfileCard = () => (
        <Card sx={{ mb: 4, borderRadius: 2, boxShadow: 3 }}>
            <CardContent>
                <Grid container spacing={3}>
                    <Grid item xs={12} md={4} sx={{ display: 'flex', justifyContent: 'center' }}>
                        <Box sx={{ position: 'relative' }}>
                            <Avatar
                                src={profilePicture}
                                sx={{
                                    width: 150,
                                    height: 150,
                                    border: '4px solid #675BC8',
                                    boxShadow: '0 4px 8px rgba(0,0,0,0.1)'
                                }}
                            />
                            {editingUserId === user?.userId && (
                                <IconButton
                                    sx={{
                                        position: 'absolute',
                                        bottom: 0,
                                        right: 0,
                                        backgroundColor: 'primary.main',
                                        '&:hover': { backgroundColor: 'primary.dark' }
                                    }}
                                    onClick={() => fileInputRef.current?.click()}
                                >
                                    <EditIcon sx={{ color: 'white' }} />
                                </IconButton>
                            )}
                        </Box>
                    </Grid>
                    <Grid item xs={12} md={8}>
                        <Typography variant="h4" gutterBottom sx={{ color: 'primary.main', fontWeight: 'bold' }}>
                            {user?.firstName} {user?.lastName}
                        </Typography>
                        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                            @{user?.username}
                        </Typography>
                        <Divider sx={{ my: 2 }} />
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6}>
                                <Typography variant="body2" color="text.secondary">Email</Typography>
                                <Typography variant="body1">{user?.email}</Typography>
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <Typography variant="body2" color="text.secondary">Phone</Typography>
                                <Typography variant="body1">{user?.phoneNumber}</Typography>
                            </Grid>
                            <Grid item xs={12}>
                                <Typography variant="body2" color="text.secondary">Address</Typography>
                                <Typography variant="body1">{user?.address}</Typography>
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );

    const renderApplications = () => (
        <Box sx={{ mt: 4 }}>
            <Tabs 
                value={tabValue} 
                onChange={handleTabChange} 
                centered
                sx={{
                    '& .MuiTab-root': {
                        fontSize: '1.1rem',
                        fontWeight: 'bold',
                        textTransform: 'none',
                        minWidth: 200,
                    },
                    '& .Mui-selected': {
                        color: 'primary.main',
                    },
                }}
            >
                <Tab label="Rehome Applications" />
                <Tab label="Adoption Applications" />
            </Tabs>

            {tabValue === 0 && (
                <Grid container spacing={3} sx={{ mt: 2 }}>
                    {rehomeApplications.map((rehome) => (
                        <Grid item xs={12} md={6} key={rehome.pid}>
                            <Card sx={{ 
                                borderRadius: 2, 
                                boxShadow: 3,
                                transition: 'transform 0.2s',
                                '&:hover': {
                                    transform: 'translateY(-4px)',
                                }
                            }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                            {rehome.name}
                                        </Typography>
                                        <Chip 
                                            label={rehome.status} 
                                            color={getStatusColor(rehome.status)}
                                            sx={{ fontWeight: 'bold' }}
                                        />
                                    </Box>
                                    <Grid container spacing={2}>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">Type</Typography>
                                            <Typography variant="body1">{rehome.type}</Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">Breed</Typography>
                                            <Typography variant="body1">{rehome.breed}</Typography>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Typography variant="body2" color="text.secondary">Submission Date</Typography>
                                            <Typography variant="body1">{rehome.submissionDate}</Typography>
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            {tabValue === 1 && (
                <Grid container spacing={3} sx={{ mt: 2 }}>
                    {adoptionApplications.map((adoption) => (
                        <Grid item xs={12} md={6} key={adoption.adoptionID}>
                            <Card sx={{ 
                                borderRadius: 2, 
                                boxShadow: 3,
                                transition: 'transform 0.2s',
                                '&:hover': {
                                    transform: 'translateY(-4px)',
                                }
                            }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                        <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                            {adoption.petType}
                                        </Typography>
                                        <Chip 
                                            label={adoption.status} 
                                            color={getStatusColor(adoption.status)}
                                            sx={{ fontWeight: 'bold' }}
                                        />
                                    </Box>
                                    <Grid container spacing={2}>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">Breed</Typography>
                                            <Typography variant="body1">{adoption.breed}</Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">Pet Name</Typography>
                                            <Typography variant="body1">{adoption.name}</Typography>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Typography variant="body2" color="text.secondary">Submission Date</Typography>
                                            <Typography variant="body1">{adoption.submissionDate}</Typography>
                                        </Grid>
                                    </Grid>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}
        </Box>
    );

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user) {
        return <div>User not found. Please log in again.</div>;
    }

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            {renderProfileCard()}
            {renderApplications()}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert 
                    onClose={handleSnackbarClose} 
                    severity={snackbar.severity}
                    sx={{ width: '100%' }}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default Profile;
