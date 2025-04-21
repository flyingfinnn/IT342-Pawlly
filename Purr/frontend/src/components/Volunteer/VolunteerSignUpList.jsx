import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography,
    Box,
    IconButton,
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';

const VolunteerSignUpList = () => {
    const [signUps, setSignUps] = useState([]);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [selectedSignUp, setSelectedSignUp] = useState(null);
    const [openConfirmationDialog, setOpenConfirmationDialog] = useState(false); // State for confirmation dialog

    useEffect(() => {
        fetchSignUps();
    }, []);

    const fetchSignUps = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/volunteer/signup');
            console.log(response.data); // Check what data you're receiving
            setSignUps(response.data);
        } catch (error) {
            console.error('Error fetching sign-ups:', error);
        }
    };

    const handleDelete = async (id) => {
        const confirmed = window.confirm('Are you sure you want to delete this sign-up?');
        if (confirmed) {
            await axios.delete(`http://localhost:8080/api/volunteer/signup/${id}`);
            fetchSignUps(); // Refresh the list
        }
    };

    const handleEditClick = (signUp) => {
        setSelectedSignUp({ ...signUp }); // Ensure we have a fresh copy
        setOpenEditDialog(true);
    };

    const handleClose = () => {
        setOpenEditDialog(false);
        setSelectedSignUp(null);
    };

    const handleSave = () => {
        setOpenConfirmationDialog(true); // Show confirmation dialog
    };

    const handleConfirmSave = async () => {
        if (selectedSignUp) {
            try {
                // Only send the fields that were actually edited (exclude opportunityId)
                const { opportunityId, ...updatedSignUp } = selectedSignUp;

                console.log('Saving updated data:', updatedSignUp);

                // Use PUT request with updated fields, without changing opportunityId
                await axios.put(`http://localhost:8080/api/volunteer/signup/${selectedSignUp.signUpID}`, updatedSignUp);
                fetchSignUps(); // Refresh the list after saving
                handleClose(); // Close the dialog
                setOpenConfirmationDialog(false); // Close the confirmation dialog
            } catch (error) {
                console.error('Error updating sign-up:', error);
            }
        }
    };

    const handleCancelSave = () => {
        setOpenConfirmationDialog(false); // Close the confirmation dialog without saving
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        // Update only the fields that should be editable
        setSelectedSignUp((prevSignUp) => ({
            ...prevSignUp,
            [name]: value,
        }));
    };

    return (
        <Box p={4}>
            <Typography variant="h4" gutterBottom>
                Volunteer Sign-Ups
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>First Name</TableCell>
                            <TableCell>Last Name</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Opportunity ID</TableCell>
                            <TableCell>Hours Worked</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {signUps.map((signUp) => (
                            <TableRow key={signUp.signUpID}>
                                <TableCell>{signUp.signUpID}</TableCell>
                                <TableCell>{signUp.firstName}</TableCell>
                                <TableCell>{signUp.lastName}</TableCell>
                                <TableCell>{signUp.email}</TableCell>
                                <TableCell>{signUp.opportunityId}</TableCell>
                                <TableCell>{signUp.hoursWorked}</TableCell>
                                <TableCell>
                                    <IconButton
                                        color="primary"
                                        onClick={() => handleEditClick(signUp)}
                                        style={{ marginRight: '8px' }}
                                    >
                                        <Edit />
                                    </IconButton>
                                    <IconButton
                                        color="secondary"
                                        onClick={() => handleDelete(signUp.signUpID)}
                                    >
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Edit Volunteer Sign-Up Dialog */}
            <Dialog open={openEditDialog} onClose={handleClose}>
                <DialogTitle>Edit Volunteer Sign-Up</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="First Name"
                        name="firstName"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={selectedSignUp ? selectedSignUp.firstName : ''}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Last Name"
                        name="lastName"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={selectedSignUp ? selectedSignUp.lastName : ''}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Email"
                        name="email"
                        type="email"
                        fullWidth
                        variant="outlined"
                        value={selectedSignUp ? selectedSignUp.email : ''}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Opportunity ID"
                        name="opportunityId"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={selectedSignUp ? selectedSignUp.opportunityId : ''}
                        disabled // Keep the Opportunity ID as read-only
                    />
                    <TextField
                        margin="dense"
                        label="Hours Worked"
                        name="hoursWorked"
                        type="number"
                        fullWidth
                        variant="outlined"
                        value={selectedSignUp ? selectedSignUp.hoursWorked : ''}
                        onChange={handleChange}
                    />
                </DialogContent>

                <DialogActions>
                    <Button onClick={handleClose} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={handleSave} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Confirmation Dialog */}
            <Dialog open={openConfirmationDialog} onClose={handleCancelSave}>
                <DialogTitle>Confirm Edit</DialogTitle>
                <DialogContent>
                    <Typography>
                        Are you sure you want to save the changes to this sign-up?
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelSave} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={handleConfirmSave} color="primary">
                        Yes
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default VolunteerSignUpList;
