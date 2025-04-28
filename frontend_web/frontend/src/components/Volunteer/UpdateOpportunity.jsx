import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { TextField, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Typography, Paper } from "@mui/material";
import axios from "axios";

const UpdateOpportunity = ({ open, onClose, opportunity }) => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        title: opportunity?.title || "",
        description: opportunity?.description || "",
        registrationStartDate: opportunity?.registrationStartDate || "",
        registrationEndDate: opportunity?.registrationEndDate || "",
        volunteerDatetime: opportunity?.volunteerDatetime || "",
        location: opportunity?.location || "",
        hoursWorked: opportunity?.hoursWorked || "0", // Ensure it's a string that represents a number
        volunteersNeeded: opportunity?.volunteersNeeded || "0", // Ensure it's a string that represents a number
    });
    const [image, setImage] = useState(null); // To handle the image upload

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleImageChange = (e) => {
        setImage(e.target.files[0]); // Set the selected image file
    };

    const handleSave = async (e) => {
        e.preventDefault(); // Prevent default form submission
        const confirmationMessage = "Are you sure you want to update this opportunity?";
    
        const confirmSave = window.confirm(confirmationMessage);
    
        if (confirmSave) {
            try {
                const formDataToSend = new FormData();
                // Append the opportunity data
                formDataToSend.append('opportunity', new Blob([JSON.stringify(formData)], { type: 'application/json' }));
                // Append the volunteer image if available
                if (image) {
                    formDataToSend.append('volunteerImage', image);
                }
    
                // Send the PUT request with form data
                const response = await axios.put(`http://localhost:8080/api/volunteer/opportunity/${opportunity.opportunityID}`, formDataToSend, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
    
                // Assuming the response contains the updated opportunity data
                const updatedOpportunity = response.data;
    
                // Navigate to the OpportunityDetail page using the updated opportunity's ID
                navigate(`/opportunity/${updatedOpportunity.opportunityID}`);
                onClose(); // Close the modal after saving
            } catch (error) {
                console.error("Error saving opportunity:", error);
            }
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
            <DialogTitle>Update Opportunity</DialogTitle>
            <DialogContent>
                <form onSubmit={handleSave}>
                    <Grid container spacing={3}>
                        <Grid item xs={12}>
                            <TextField
                                label="Title"
                                variant="outlined"
                                fullWidth
                                name="title"
                                value={formData.title}
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Description"
                                variant="outlined"
                                multiline
                                rows={4}
                                fullWidth
                                name="description"
                                value={formData.description}
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField
                                label="Registration Start Date"
                                variant="outlined"
                                type="datetime-local"
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                                name="registrationStartDate"
                                value={formData.registrationStartDate}
                                min={formData.registrationStartDate || new Date().toISOString().slice(0, 16)} // Set minimum date as the current date
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField
                                label="Registration End Date"
                                variant="outlined"
                                type="datetime-local"
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                                name="registrationEndDate"
                                value={formData.registrationEndDate}
                                min={formData.registrationEndDate || new Date().toISOString().slice(0, 16)} // Set minimum date as the current date
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Volunteer Date/Time"
                                variant="outlined"
                                type="datetime-local"
                                InputLabelProps={{ shrink: true }}
                                fullWidth
                                name="volunteerDatetime"
                                value={formData.volunteerDatetime}
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Location"
                                variant="outlined"
                                fullWidth
                                name="location"
                                value={formData.location}
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField
                                label="Hours Worked"
                                variant="outlined"
                                type="number"
                                fullWidth
                                name="hoursWorked"
                                value={formData.hoursWorked}
                                onChange={handleChange}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField
                                label="Volunteers Needed"
                                variant="outlined"
                                type="number"
                                fullWidth
                                name="volunteersNeeded"
                                value={formData.volunteersNeeded}
                                onChange={handleChange}
                            />
                        </Grid>
                        {/* File input for image */}
                        <Grid item xs={12}>
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleImageChange}
                                style={{ width: "100%" }}
                            />
                        </Grid>
                    </Grid>
                </form>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleSave} color="primary">
                    Save Changes
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateOpportunity;
