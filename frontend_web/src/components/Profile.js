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
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    DialogContentText,
    Badge,
    Menu,
    MenuItem,
} from "@mui/material";
import { useUser } from "./UserContext";
import { Visibility, VisibilityOff, Edit as EditIcon, Save as SaveIcon, Cancel as CancelIcon, Delete as DeleteIcon, MoreVert as MoreVertIcon, AdminPanelSettings as AdminIcon } from "@mui/icons-material";
import { useNavigate } from 'react-router-dom';

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
    const navigate = useNavigate();
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [menuAnchorEl, setMenuAnchorEl] = useState(null);
    const [selectedApplication, setSelectedApplication] = useState(null);
    const [editApplication, setEditApplication] = useState(null);

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

    const formatMemberSince = (date) => {
        return new Date(date).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    const handleDeleteAccount = async () => {
        try {
            const token = localStorage.getItem("token");
            await axios.delete(
                `${process.env.REACT_APP_BACKEND_URL}/api/users/${user.userId}`,
                {
                    headers: { Authorization: `Bearer ${token}` }
                }
            );
            localStorage.removeItem("token");
            navigate("/login");
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Error deleting account. Please try again.",
                severity: "error"
            });
        }
    };

    const handleDeleteApplication = async (id, type) => {
        try {
            const token = localStorage.getItem("token");
            const endpoint = type === 'rehome' 
                ? `/api/pet/deletePet/${id}`
                : `/api/adoptions/${id}`;
            
            await axios.delete(
                `${process.env.REACT_APP_BACKEND_URL}${endpoint}`,
                {
                    headers: { Authorization: `Bearer ${token}` }
                }
            );

            if (type === 'rehome') {
                setRehomeApplications(prev => prev.filter(app => app.pid !== id));
            } else {
                setAdoptionApplications(prev => prev.filter(app => app.adoptionID !== id));
            }

            setSnackbar({
                open: true,
                message: "Application deleted successfully",
                severity: "success"
            });
        } catch (error) {
            setSnackbar({
                open: true,
                message: "Error deleting application",
                severity: "error"
            });
        }
    };

    const handleUpdateApplication = async (updatedApplication) => {
        try {
            const token = localStorage.getItem("token");
            const endpoint = updatedApplication.type === 'rehome' 
                ? `/api/pet/putPetDetails`
                : `/api/adoptions/${updatedApplication.adoptionID}`;
            
            const response = await axios.put(
                `${process.env.REACT_APP_BACKEND_URL}${endpoint}`,
                updatedApplication,
                {
                    headers: { 
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            if (response.status === 200) {
                // Update the local state
                if (updatedApplication.type === 'rehome') {
                    setRehomeApplications(prev => 
                        prev.map(app => app.pid === updatedApplication.pid ? updatedApplication : app)
                    );
                } else {
                    setAdoptionApplications(prev => 
                        prev.map(app => app.adoptionID === updatedApplication.adoptionID ? updatedApplication : app)
                    );
                }

                setSnackbar({
                    open: true,
                    message: "Application updated successfully",
                    severity: "success"
                });
            }
        } catch (error) {
            console.error("Error updating application:", error);
            setSnackbar({
                open: true,
                message: "Error updating application. Please try again.",
                severity: "error"
            });
        }
    };

    const handleEditApplicationChange = (field, value) => {
        setEditApplication(prev => ({
            ...prev,
            [field]: value
        }));
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
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                            <Box>
                                {editingUserId === user?.userId ? (
                                    <TextField
                                        fullWidth
                                        name="firstName"
                                        value={editFormData.firstName}
                                        onChange={handleEditChange}
                                        sx={{ mb: 1 }}
                                    />
                                ) : (
                                    <Typography variant="h4" gutterBottom sx={{ color: 'primary.main', fontWeight: 'bold' }}>
                                        {user?.firstName} {user?.lastName}
                                    </Typography>
                                )}
                                <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                                    @{user?.username}
                                </Typography>
                                <Typography variant="body2" color="text.secondary">
                                    Member since {formatMemberSince(user?.createdAt)}
                                </Typography>
                            </Box>
                            <Box>
                                {editingUserId === user?.userId ? (
                                    <>
                                        <Button
                                            variant="contained"
                                            color="primary"
                                            startIcon={<SaveIcon />}
                                            onClick={handleSaveEdit}
                                            sx={{ mr: 1 }}
                                        >
                                            Save
                                        </Button>
                                        <Button
                                            variant="outlined"
                                            color="error"
                                            startIcon={<CancelIcon />}
                                            onClick={handleCancelEdit}
                                        >
                                            Cancel
                                        </Button>
                                    </>
                                ) : (
                                    <Button
                                        variant="outlined"
                                        startIcon={<EditIcon />}
                                        onClick={handleEdit}
                                    >
                                        Edit Profile
                                    </Button>
                                )}
                                <Button
                                    variant="outlined"
                                    color="error"
                                    startIcon={<DeleteIcon />}
                                    onClick={() => setDeleteDialogOpen(true)}
                                >
                                    Delete Account
                                </Button>
                            </Box>
                        </Box>
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
                        {user?.role === 'ROLE_ADMIN' && (
                            <Button
                                variant="contained"
                                startIcon={<AdminIcon />}
                                sx={{ mt: 2 }}
                                onClick={() => navigate('/admin')}
                            >
                                Admin Dashboard
                            </Button>
                        )}
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );

    const renderApplicationCard = (application, type) => (
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
                        {type === 'rehome' ? application.name : application.petType}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Chip 
                            label={application.status} 
                            color={getStatusColor(application.status)}
                            sx={{ fontWeight: 'bold' }}
                        />
                        <IconButton
                            onClick={(e) => {
                                setMenuAnchorEl(e.currentTarget);
                                setSelectedApplication({ ...application, type });
                            }}
                        >
                            <MoreVertIcon />
                        </IconButton>
                    </Box>
                </Box>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Box
                            component="img"
                            src={`${process.env.REACT_APP_BACKEND_URL}${application.photo}`}
                            alt={type === 'rehome' ? application.name : application.petType}
                            sx={{
                                width: '100%',
                                height: 200,
                                objectFit: 'cover',
                                borderRadius: 1,
                                mb: 2
                            }}
                        />
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Type</Typography>
                        <Typography variant="body1">{type === 'rehome' ? application.type : application.petType}</Typography>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">Breed</Typography>
                        <Typography variant="body1">{application.breed}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant="body2" color="text.secondary">Submission Date</Typography>
                        <Typography variant="body1">{application.submissionDate}</Typography>
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
                            {renderApplicationCard(rehome, 'rehome')}
                        </Grid>
                    ))}
                </Grid>
            )}

            {tabValue === 1 && (
                <Grid container spacing={3} sx={{ mt: 2 }}>
                    {adoptionApplications.map((adoption) => (
                        <Grid item xs={12} md={6} key={adoption.adoptionID}>
                            {renderApplicationCard(adoption, 'adoption')}
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
            
            {/* Delete Account Dialog */}
            <Dialog
                open={deleteDialogOpen}
                onClose={() => setDeleteDialogOpen(false)}
            >
                <DialogTitle>Delete Account</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete your account? This action cannot be undone and will permanently delete all your data.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleDeleteAccount} color="error" variant="contained">
                        Delete Account
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Application Menu */}
            <Menu
                anchorEl={menuAnchorEl}
                open={Boolean(menuAnchorEl)}
                onClose={() => setMenuAnchorEl(null)}
            >
                <MenuItem onClick={() => {
                    setEditApplication(selectedApplication);
                    setMenuAnchorEl(null);
                }}>
                    <EditIcon sx={{ mr: 1 }} /> Edit
                </MenuItem>
                <MenuItem onClick={() => {
                    handleDeleteApplication(
                        selectedApplication?.pid || selectedApplication?.adoptionID,
                        selectedApplication?.type
                    );
                    setMenuAnchorEl(null);
                }}>
                    <DeleteIcon sx={{ mr: 1 }} /> Delete
                </MenuItem>
            </Menu>

            {/* Edit Application Dialog */}
            <Dialog
                open={Boolean(editApplication)}
                onClose={() => setEditApplication(null)}
                maxWidth="sm"
                fullWidth
            >
                <DialogTitle>Edit Application</DialogTitle>
                <DialogContent>
                    <Box sx={{ mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Name"
                            value={editApplication?.name || ''}
                            onChange={(e) => handleEditApplicationChange('name', e.target.value)}
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            fullWidth
                            label="Type"
                            value={editApplication?.type || editApplication?.petType || ''}
                            onChange={(e) => handleEditApplicationChange('type', e.target.value)}
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            fullWidth
                            label="Breed"
                            value={editApplication?.breed || ''}
                            onChange={(e) => handleEditApplicationChange('breed', e.target.value)}
                            sx={{ mb: 2 }}
                        />
                        <TextField
                            fullWidth
                            label="Description"
                            multiline
                            rows={4}
                            value={editApplication?.description || ''}
                            onChange={(e) => handleEditApplicationChange('description', e.target.value)}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setEditApplication(null)}>Cancel</Button>
                    <Button 
                        onClick={() => {
                            handleUpdateApplication(editApplication);
                            setEditApplication(null);
                        }} 
                        color="primary"
                    >
                        Save Changes
                    </Button>
                </DialogActions>
            </Dialog>

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