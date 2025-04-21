import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Typography,
    Box,
    Grid,
    Paper,
    Button,
    Snackbar,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
} from '@mui/material';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import EventIcon from '@mui/icons-material/Event';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import GroupIcon from '@mui/icons-material/Group';
import { motion } from 'framer-motion';
import AuthModal from '../AuthModal';
import NotificationsActiveIcon from '@mui/icons-material/NotificationsActive';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import UpdateOpportunityModal from "./UpdateOpportunity";
import Swal from 'sweetalert2';
import EditIcon from '@mui/icons-material/Edit';
import UserProfile from './UserProfile';
import User from '../User';

const OpportunityDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [opportunity, setOpportunity] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        address: '',
        phoneNumber: '',
    });
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const [openDialog, setOpenDialog] = useState(false);
    const [authModalOpen, setAuthModalOpen] = useState(false);
    const [userDetails, setUserDetails] = useState(null); // New state for user details
    const [isFormValid, setIsFormValid] = useState(false); // Validation state for the form
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false); // Confirmation dialog state
    const [filter, setFilter] = useState('All'); // Default filter is 'All'
    const [signUpCount, setSignUpCount] = useState(0); // New state for the number of sign-ups


    const { opportunityId } = useParams();

    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleEditButtonClick = () => {
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    //fetch number of sign-ups
    const fetchSignUpCount = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/volunteer/opportunity/${id}/signups`);
            setSignUpCount(response.data); // Set the number of sign-ups
        } catch (error) {
            console.error('Error fetching sign-up count:', error);
        }
    };

    // Fetch opportunity details
    const fetchOpportunityDetail = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/volunteer/opportunity/${id}`);
            setOpportunity(response.data);
            fetchSignUpCount();
        } catch (error) {
            console.error('Error fetching opportunity details:', error);
            setError('Could not fetch opportunity details.');
        } finally {
            setLoading(false);
        }
    };

    // Fetch user details (if logged in)
    const fetchUserDetails = async () => {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const response = await axios.get('http://localhost:8080/api/users/me', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setUserDetails(response.data);
                // Update form data with user details after fetching
                setFormData({
                    firstName: response.data.firstName || '',
                    lastName: response.data.lastName || '',
                    email: response.data.email || '',
                    address: response.data.address || '',
                    phoneNumber: response.data.phoneNumber || '',
                });
            } catch (error) {
                console.error('Error fetching user details:', error);
            }
        }
    };

    useEffect(() => {
        fetchOpportunityDetail();
        fetchUserDetails(); // Fetch user details when the component mounts
    }, [id]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleFilterChange = (event) => {
        setFilter(event.target.value);
    };

    // Check if the form is valid
    const checkFormValidity = () => {
        const isValid = Object.values(formData).every(value => value.trim() !== '');
        setIsFormValid(isValid); // Update state for form validity
        return isValid; // Return the validity for conditional checks
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        // Validate form inputs
        const isValid = Object.values(formData).every(value => value.trim() !== '');

        if (isValid) {
            setConfirmDialogOpen(true); // Show confirmation dialog if form is valid
        } else {
            // Use SweetAlert2 for the error message
            Swal.fire({
                icon: 'error',
                title: 'Incomplete Form',
                text: 'Please fill all required fields.',
                confirmButtonText: 'OK',
            });
        }
    };;

    const handleConfirmSubmit = async () => {
        try {
            const dataToSubmit = {
                ...formData,
                opportunityID: id,
                hoursWorked: opportunity.hoursWorked,
            };

            // Make the API call
            await axios.post(`http://localhost:8080/api/volunteer/signup/${id}`, dataToSubmit);

            // Success alert
            Swal.fire({
                icon: 'success',
                title: 'Success!',
                text: 'You have successfully signed up for the opportunity!',
                confirmButtonText: 'OK',
                timer: 2000,
                timerProgressBar: true,
            });

            // Redirect after the SweetAlert2 popup closes
            setTimeout(() => navigate('/volunteer'), 2000);
        } catch (error) {
            if (error.response && error.response.status === 409) {
                // Duplicate sign-up detected alert
                Swal.fire({
                    icon: 'warning',
                    title: 'Duplicate Sign-Up',
                    text: 'You are already registered for this opportunity.',
                    confirmButtonText: 'OK',
                });
            } else {
                // General error alert
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: 'An error occurred while registering for the event. Please try again later.',
                    confirmButtonText: 'OK',
                });
            }
            console.error('Error registering for the event:', error);
        } finally {
            setConfirmDialogOpen(false); // Close the confirmation dialog
            setOpenDialog(false);
        }
    };

    const handleDialogClose = () => setOpenDialog(false);
    const handleConfirmDialogClose = () => setConfirmDialogOpen(false);
    const handleSnackbarClose = () => setSnackbarOpen(false);

    const handleRegisterButtonClick = () => {
        // Check if the user is logged in and has a userId
        const user = JSON.parse(localStorage.getItem('user'));

        if (user && user.userId) {
            const userId = user.userId;

            // Compare the logged-in userId with the opportunity creatorId
            if (opportunity.creatorId === userId) {
                Swal.fire({
                    icon: 'warning',
                    title: 'Registration Not Allowed',
                    text: 'You cannot register your own opportunity.',
                    confirmButtonText: 'OK',
                });
                return; // Prevent the registration process if the user is the creator
            }

            // Proceed with the registration process if the user is not the creator
            const token = localStorage.getItem("token");
            if (!token) {
                setAuthModalOpen(true); // Open the auth modal if the user is not logged in
            } else {
                setOpenDialog(true); // Proceed with the registration dialog if logged in
            }
        } else {
            setAuthModalOpen(true); // Open the auth modal if no user is logged in
        }
    };


    if (loading) return <Typography>Loading...</Typography>;
    if (error) return <Typography>{error}</Typography>;
    if (!opportunity) return <Typography>No opportunity found.</Typography>;

    const isRegistrationOpen =
        opportunity.registrationStartDate &&
        opportunity.registrationEndDate &&
        new Date() >= new Date(opportunity.registrationStartDate) &&
        new Date() <= new Date(opportunity.registrationEndDate);


    const formatDate = (date) => {
        const options = { weekday: 'long', year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit', hour12: true };
        const formattedDate = new Date(date).toLocaleDateString('en-US', options);
        return formattedDate.replace(',', ' at');
    };
    
    return (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 1 }}>
            <Grid container spacing={3} sx={{ paddingTop: 5, paddingX: 10 }}>
                <Grid item xs={12}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>

                    </Box>
                </Grid>


                {/* Opportunity Details Section */}
                <Grid container spacing={3} sx={{ paddingTop: 5, paddingX: 5 }}>
                    <Grid item xs={12} md={7}>
                        <Paper elevation={0} sx={{ padding: 3, borderRadius: 2, marginBottom: 0.5 }}>
                            <Box
                                sx={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'space-between',
                                    marginBottom: 2,
                                }}
                            >
                                <Typography
                                    variant="h4"
                                    sx={{
                                        fontFamily: "'Montserrat', sans-serif",
                                        fontWeight: "bold",
                                        color: "#675bc8",
                                    }}
                                >
                                    {opportunity.title}
                                </Typography>
                                {userDetails ? (
                                    <Button
                                        onClick={opportunity.creatorId === userDetails.userId ? handleEditButtonClick : null}
                                        variant="text"
                                        disabled={opportunity.creatorId !== userDetails.userId}
                                        sx={{
                                            marginLeft: 2,
                                            minWidth: 0,
                                            padding: 0,
                                            '&:hover': {
                                                background: 'transparent',
                                                transform: opportunity.creatorId === userDetails.userId ? 'scale(1.1)' : 'none',
                                            },
                                        }}
                                    >
                                        <EditIcon
                                            sx={{
                                                fontSize: 32,
                                                color: opportunity.creatorId === userDetails.userId ? '#675bc8' : 'gray',
                                                transition: 'transform 0.2s',
                                            }}
                                        />
                                    </Button>
                                ) : (
                                    <Typography variant="body2" sx={{ color: 'gray', fontStyle: 'italic' }}>
                                        Loading user details...
                                    </Typography>
                                )}
                            </Box>

                            {/* Modal for editing opportunity */}
                            <UpdateOpportunityModal
                                open={isModalOpen}
                                onClose={handleCloseModal} // Close modal
                                opportunity={opportunity}
                            />
                            <Typography variant="body1" sx={{ marginBottom: 0 }}>
                                {opportunity.description}
                            </Typography>
                        </Paper>
                    </Grid>


                    <Grid item xs={12} md={7} sx={{ display: 'flex', flexDirection: 'column' }}>
                        <Paper elevation={0} sx={{ padding: 3, borderRadius: 2, flex: 1 }}>
                            <Box sx={{ marginBottom: 3 }}>
                                <img
                                    src={opportunity.volunteerImageUrl ? `http://localhost:8080${opportunity.volunteerImageUrl}` : "http://localhost:3000/images/default.png"}
                                    alt="Opportunity"
                                    style={{ width: '100%', height: '550px', objectFit: 'cover', borderRadius: 8 }}
                                />
                            </Box>

                            <Box sx={{ padding: 3, borderRadius: 2, border: '1px solid lightgray' }}>
                                {/* Location */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 2 }}>
                                    <LocationOnIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Location:</strong> {opportunity.location}
                                    </Typography>
                                </Box>

                                {/* Event Date */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 2 }}>
                                    <EventIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Event Date:</strong> {new Date(opportunity.volunteerDatetime).toLocaleString()}
                                    </Typography>
                                </Box>

                                {/* Hours */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 2 }}>
                                    <AccessTimeIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Hours:</strong> {opportunity.hoursWorked}
                                    </Typography>
                                </Box>

                                {/* Volunteers Needed */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 2 }}>
                                    <GroupIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Volunteers Needed:</strong> {opportunity.volunteersNeeded}
                                    </Typography>
                                </Box>

                                {/* Sign-Ups */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 1 }}>
                                    <GroupIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Sign-Ups:</strong> {signUpCount}
                                    </Typography>
                                </Box>

                                {/* Registration Period with Register Button inline */}
                                <Box sx={{ display: 'flex', alignItems: 'center', marginBottom: 1 }}>
                                    <CalendarTodayIcon sx={{ marginRight: 1, color: '#4c3dcb' }} />
                                    <Typography variant="body2" sx={{ color: '#675bc8' }}>
                                        <strong>Registration Period:</strong> {new Date(opportunity.registrationStartDate).toLocaleString()} - {new Date(opportunity.registrationEndDate).toLocaleString()}
                                    </Typography>

                                    {/* Register Button */}
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={handleRegisterButtonClick}
                                        disabled={!isRegistrationOpen || signUpCount >= opportunity.volunteersNeeded}
                                        sx={{ marginLeft: 'auto', marginBottom: 1 }}
                                    >
                                        {signUpCount >= opportunity.volunteersNeeded ? 'Closed' : (isRegistrationOpen ? 'Register' : 'Closed')}
                                    </Button>
                                </Box>

                                <AuthModal
                                    open={authModalOpen}
                                    handleClose={() => setAuthModalOpen(false)} // Close the auth modal when the user cancels
                                />
                            </Box>
                        </Paper>
                    </Grid>

                    {/* Reminder */}
                    <Grid item xs={12} md={5} sx={{ display: 'flex', flexDirection: 'column', height: '300px', marginTop: 3 }}>
                        <Paper
                            elevation={0}
                            sx={{
                                padding: 5,
                                borderRadius: 2,
                                flex: 1,
                                background: '#F5F5F5',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                            }}
                        >
                            {/* Icon at the top */}
                            <Box sx={{ marginTop: 2, marginBottom: 2 }}>
                                <NotificationsActiveIcon sx={{ fontSize: 100, color: '#675bc8' }} /> {/* Match color to button */}
                            </Box>

                            {/* Title */}
                            <Typography
                                variant="h5"
                                gutterBottom
                                align="center"
                                sx={{
                                    fontWeight: 'bold',
                                    color: '#675bc8', // Match color to button
                                }}
                            >
                                Reminders for Volunteers
                            </Typography>

                            <Typography
                                variant="body2"  // Changed from body1 to body2 for smaller text
                                color="textPrimary"
                                align="center"
                                sx={{
                                    marginTop: 2,  // Reduced marginTop
                                    paddingX: 2,
                                    fontWeight: 'bold',
                                    fontSize: '0.875rem',  // Smaller text size
                                    lineHeight: 1.4,  // Tighter line height for readability
                                }}
                            >
                                Don't forget to join the volunteer orientation on {formatDate(opportunity.volunteerDatetime)} at {opportunity.location}. During the session, we’ll cover the event schedule, team assignments, and safety protocols.
                                <br />
                                <Typography
                                    variant="body2"  // Changed from body1 to body2 for smaller text
                                    color="textSecondary"
                                    align="center"
                                    sx={{
                                        marginTop: 1,  // Reduced marginTop
                                        paddingX: 2,
                                        fontSize: '0.875rem',  // Smaller text size for the reminder
                                        fontWeight: 'normal',
                                        lineHeight: 1.5,  // Adjust lineHeight for readability
                                    }}
                                >
                                    Here’s what to prepare:
                                </Typography>
                                <ul style={{ textAlign: 'left', margin: '8px auto', maxWidth: '450px', fontSize: '0.8rem', lineHeight: 1.6 }}>
                                    <li>Review the volunteer code of conduct beforehand.</li>
                                    <li>Bring a valid ID for registration verification.</li>
                                    <li>Wear comfortable clothes and closed-toe shoes for safety.</li>
                                    <li>Pack water and snacks to keep yourself energized.</li>
                                </ul>
                                <Typography
                                    variant="body2"
                                    color="textPrimary"
                                    align="center"
                                    sx={{
                                        marginTop: 2,
                                        fontSize: '0.875rem',  // Smaller text for the concluding note
                                        fontWeight: 'bold',
                                    }}
                                >
                                    If you have questions, feel free to reach out to the volunteer coordinator below before the event. Let's make a difference together!
                                </Typography>

                            </Typography>
                        </Paper>

                        <Paper
                            elevation={3}
                            sx={{
                                padding: 0, // Reduced padding inside the Paper
                                borderRadius: 2,
                                maxWidth: 700,
                                margin: '0',
                                backgroundColor: '#F4F4FB',
                                marginTop: 4,  // Added marginTop for spacing
                            }}
                            
                        >
                            <Box sx={{ padding: 0 }}>
                                  {/* Reduced padding inside Box */}
                                <UserProfile />
                            </Box>
                        </Paper>

                    </Grid>



                </Grid>

                <Snackbar
                    open={snackbarOpen}
                    autoHideDuration={3000}
                    onClose={handleSnackbarClose}
                    message={successMessage}
                />


                <Dialog open={openDialog} onClose={handleDialogClose} maxWidth="xs" fullWidth>
                    <DialogTitle sx={{ textAlign: 'center', fontWeight: 'bold', marginTop: 3 }}>Register for Opportunity</DialogTitle>
                    <DialogContent sx={{ padding: '20px 30px' }}>
                        {['firstName', 'lastName', 'email', 'address', 'phoneNumber'].map((field) => (
                            <TextField
                                key={field}
                                label={field.replace(/([A-Z])/g, ' $1')}
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                name={field}
                                value={formData[field]}
                                onChange={handleInputChange}
                                error={formData[field] === ''} // Set error if field is empty
                                helperText={formData[field] === '' ? 'This field is required' : ''}
                                sx={{
                                    '& .MuiInputBase-root': {
                                        borderRadius: '8px', // Rounded corners for inputs
                                        backgroundColor: '#f9f9f9', // Subtle background color for the input fields
                                    },
                                    '& .MuiFormLabel-root': {
                                        fontWeight: '500', // Lighter font weight for labels
                                    },
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderRadius: '8px', // Ensure the border is also rounded
                                        },
                                        '&:hover fieldset': {
                                            borderColor: 'primary.main', // Change border color on hover
                                        },
                                    },
                                }}
                            />
                        ))}
                    </DialogContent>

                    <DialogActions sx={{ justifyContent: 'space-between', padding: '15px 30px 40px ' }}>
                        <Button
                            onClick={handleDialogClose}
                            color="secondary"
                            variant="outlined" // Set the outlined variant
                            sx={{
                                borderRadius: '8px',
                                borderColor: '#f44336', // Red border for the cancel button
                                color: '#f44336', // Red text for cancel
                                padding: '10px 20px',
                                fontWeight: '500',
                                width: '40%',
                                '&:hover': {
                                    borderColor: '#d32f2f', // Darker red border on hover
                                    backgroundColor: '#f8d7da', // Light red background on hover
                                },
                            }}
                        >
                            Cancel
                        </Button>

                        <Button
                            onClick={handleSubmit}
                            color="primary"
                            variant="outlined" // Set the outlined variant
                            disabled={!Object.values(formData).every(value => value.trim() !== '')}
                            sx={{
                                borderRadius: '8px',
                                borderColor: '#3f51b5', // Blue border for the submit button
                                color: '#3f51b5', // Blue text for submit
                                padding: '10px 20px',
                                fontWeight: '500',
                                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)', // Subtle shadow for the button
                                width: '40%',
                                '&:hover': {
                                    borderColor: '#303f9f', // Darker blue border on hover
                                    backgroundColor: '#e8eaf6', // Light blue background on hover
                                    color: '#303f9f', // Darker text color on hover
                                },
                                '&.Mui-disabled': {
                                    borderColor: '#c5cae9', // Lighter border color when disabled
                                    color: '#c5cae9', // Lighter text color when disabled
                                },
                            }}
                        >
                            Submit
                        </Button>
                    </DialogActions>

                </Dialog>


                <Dialog
                    open={confirmDialogOpen}
                    onClose={handleConfirmDialogClose}
                >
                    <DialogTitle>Confirm Submission</DialogTitle>
                    <DialogContent>
                        <Typography variant="body1">Are you sure you want to submit your registration?</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleConfirmDialogClose} color="secondary">
                            Cancel
                        </Button>
                        <Button onClick={handleConfirmSubmit} color="primary">
                            Confirm
                        </Button>
                    </DialogActions>
                </Dialog>
            </Grid>
        </motion.div>
    );
};

export default OpportunityDetail;
