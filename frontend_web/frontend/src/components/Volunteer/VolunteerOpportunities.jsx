import React, { useEffect, useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Container, Typography, Grid, Card, CardContent, CardActions, IconButton, CardMedia } from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import axios from 'axios';

const VolunteerOpportunities = () => {
    const [volunteerOpportunities, setVolunteerOpportunities] = useState([]);
    const [open, setOpen] = useState(false);
    const [selectedOpportunity, setSelectedOpportunity] = useState({
        title: '',
        description: '',
        date: '',
        location: '',
        hoursWorked: 0,
        volunteersNeeded: 0,
        registrationStartDate: '',
        registrationEndDate: '',
        volunteerDatetime: '',
        creatorId: 0,
        opportunityID: null,
        volunteerImage: null // Added for the image
    });
    const [image, setImage] = useState(null); // New state to handle the image

    useEffect(() => {
        fetchVolunteerOpportunities();
    }, []);

    const fetchVolunteerOpportunities = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/volunteer/opportunities');
            setVolunteerOpportunities(response.data);
        } catch (error) {
            console.error("Error fetching volunteer opportunities:", error);
        }
    };

    const handleOpen = (opportunity) => {
        setSelectedOpportunity(opportunity);
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setSelectedOpportunity({
            title: '',
            description: '',
            date: '',
            location: '',
            hoursWorked: 0,
            volunteersNeeded: 0,
            registrationStartDate: '',
            registrationEndDate: '',
            volunteerDatetime: '',
            creatorId: 0,
            opportunityID: null,
            volunteerImage: null // Reset image state
        });
        setImage(null); // Clear image on close
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setSelectedOpportunity((prev) => ({ ...prev, [name]: value }));
    };

    const handleImageChange = (e) => {
        setImage(e.target.files[0]); // Set the selected image file
    };

    const handleSave = async () => {
        const confirmationMessage = "Are you sure you want to update this opportunity?";

        const confirmSave = window.confirm(confirmationMessage);

        if (confirmSave) {
            try {
                const formData = new FormData();
                // Append the other opportunity data
                formData.append('opportunity', new Blob([JSON.stringify(selectedOpportunity)], { type: 'application/json' }));
                // Append the volunteer image if available
                if (image) {
                    formData.append('volunteerImage', image);
                }

                // Send the PUT request with form data
                await axios.put(`http://localhost:8080/api/volunteer/opportunity/${selectedOpportunity.opportunityID}`, formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                fetchVolunteerOpportunities();
                handleClose();
                window.location.reload();
            } catch (error) {
                console.error("Error saving opportunity:", error);
            }
        }
    };

    const handleDelete = async (id) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this opportunity?");
        if (confirmDelete) {
            try {
                await axios.delete(`http://localhost:8080/api/volunteer/opportunity/${id}`);
                fetchVolunteerOpportunities();
            } catch (error) {
                console.error("Error deleting opportunity:", error);
            }
        }
    };

    // Get today's date and time in ISO format for min attributes
    const today = new Date().toISOString().slice(0, 16); // Local date and time (YYYY-MM-DDTHH:mm)

    return (
        <Container maxWidth="lg">
            <br />
            <Typography variant="h4" gutterBottom>
                Volunteer Opportunities Admin Dashboard
            </Typography>
            <Grid container spacing={3} style={{ marginTop: '20px' }}>
                {volunteerOpportunities.map((opportunity) => (
                    <Grid item xs={12} sm={6} md={4} key={opportunity.opportunityID}>
                        <Card variant="outlined" sx={{ height: '100%' }}>
                            <CardMedia
                                component="img"
                                height="140"
                                image={
                                    opportunity.volunteerImageUrl
                                        ? `http://localhost:8080${opportunity.volunteerImageUrl}`  // Use the volunteerImageUrl if available
                                        : 'http://localhost:3000/images/default.png'  // Fallback to default image if not available
                                }
                                alt={opportunity.title}
                            />
                            <CardContent>
                                <Typography variant="h4">{opportunity.title}</Typography>
                                <Typography variant="h6">Opportunity ID: {opportunity.opportunityID}</Typography>
                                {/* Display the Creator ID */}
                                <Typography variant="body2">Creator ID: {opportunity.creatorId}</Typography>
                                <Typography variant="body2">Description: {opportunity.description}</Typography>
                                <Typography variant="body2">Event Date:
                                    {opportunity.volunteerDatetime ? new Date(opportunity.volunteerDatetime).toLocaleDateString() : "No date provided"}
                                </Typography>
                                <Typography variant="body2">Location: {opportunity.location}</Typography>
                                <Typography variant="body2">
                                    Hours Worked: {opportunity.hoursWorked !== undefined ? opportunity.hoursWorked : "Not specified"}
                                </Typography>
                                <Typography variant="body2">
                                    Volunteers Needed: {opportunity.volunteersNeeded !== undefined ? opportunity.volunteersNeeded : "Not specified"}
                                </Typography>
                            </CardContent>
                            <CardActions>
                                <IconButton onClick={() => handleOpen(opportunity)}>
                                    <Edit sx={{ color: '#1976d2' }} />
                                </IconButton>
                                <IconButton onClick={() => handleDelete(opportunity.opportunityID)}>
                                    <Delete sx={{ color: '#f44336' }} />
                                </IconButton>
                            </CardActions>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Update Volunteer Opportunity</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Title"
                        name="title"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.title}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Description"
                        name="description"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.description}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Location"
                        name="location"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.location}
                        onChange={handleChange}
                    />
                    <TextField
                        margin="dense"
                        label="Hours Worked"
                        name="hoursWorked"
                        type="number"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.hoursWorked}
                        onChange={handleChange}
                        inputProps={{ min: 1 }} // Set minimum value to 1
                        onWheel={(e) => e.target.blur()} // Prevent scrolling to change value
                    />
                    <Typography variant="body2">Selected Hours Worked: {selectedOpportunity.hoursWorked}</Typography>

                    <TextField
                        margin="dense"
                        label="Volunteers Needed"
                        name="volunteersNeeded"
                        type="number"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.volunteersNeeded}
                        onChange={handleChange}
                        inputProps={{ min: 1 }} // Set minimum value to 1
                        onWheel={(e) => e.target.blur()} // Prevent scrolling to change value
                    />
                    <Typography variant="body2">Selected Volunteers Needed: {selectedOpportunity.volunteersNeeded}</Typography>

                    {/* Registration Start Date */}
                    <TextField
                        margin="dense"
                        label="Registration Start Date"
                        name="registrationStartDate"
                        type="datetime-local"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.registrationStartDate}
                        onChange={handleChange}
                        inputProps={{ min: today }} // Set min to current date and time
                    />

                    {/* Registration End Date */}
                    <TextField
                        margin="dense"
                        label="Registration End Date"
                        name="registrationEndDate"
                        type="datetime-local"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.registrationEndDate}
                        onChange={handleChange}
                        inputProps={{ min: selectedOpportunity.registrationStartDate || today }} // Min is the registration start date or now
                    />

                    {/* Volunteer Date/Time */}
                    <TextField
                        margin="dense"
                        label="Volunteer Date/Time"
                        name="volunteerDatetime"
                        type="datetime-local"
                        fullWidth
                        variant="outlined"
                        value={selectedOpportunity.volunteerDatetime}
                        onChange={handleChange}
                        inputProps={{ min: selectedOpportunity.registrationStartDate || today }} // Min is the registration start date or now
                    />

                    {/* Image Upload */}
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        style={{ marginTop: '10px' }}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleSave} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default VolunteerOpportunities;
