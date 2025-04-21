import React, { useState } from "react";
import {
    TextField,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Grid,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
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
        hoursWorked: opportunity?.hoursWorked || "0",
        volunteersNeeded: opportunity?.volunteersNeeded || "0",
    });
    const [image, setImage] = useState(null);
    const [dateErrors, setDateErrors] = useState({
        registrationStartDate: '',
        registrationEndDate: '',
        volunteerDatetime: '',
    });
    const [descriptionError, setDescriptionError] = useState('');
    const [isConfirmDialogOpen, setIsConfirmDialogOpen] = useState(false);
    

    const today = new Date().toISOString().slice(0, 16);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));

        if (name === 'description') {
            const wordCount = value.trim().split(/\s+/).length;
            setDescriptionError(
                wordCount > 500 ? 'Description exceeds the maximum word limit of 500.' : ''
            );
        }

        if (['registrationStartDate', 'registrationEndDate', 'volunteerDatetime'].includes(name)) {
            const errors = { ...dateErrors };

            if (name === 'registrationStartDate' && value) {
                errors.registrationStartDate = '';
                if (formData.registrationEndDate && new Date(value) >= new Date(formData.registrationEndDate)) {
                    errors.registrationEndDate = 'Start date must be before the end date.';
                }
            }

            if (name === 'registrationEndDate' && value) {
                errors.registrationEndDate = '';
                if (new Date(value) <= new Date(formData.registrationStartDate)) {
                    errors.registrationEndDate = 'End date must be after the start date.';
                }
                if (formData.volunteerDatetime && new Date(value) >= new Date(formData.volunteerDatetime)) {
                    errors.volunteerDatetime = 'End date must be before the volunteer date.';
                }
            }

            if (name === 'volunteerDatetime' && value) {
                errors.volunteerDatetime = '';
                if (new Date(value) <= new Date(formData.registrationEndDate)) {
                    errors.volunteerDatetime = 'Volunteer date must be after the registration end date.';
                }
            }

            setDateErrors(errors);
        }
    };

    const handleImageChange = (e) => {
        setImage(e.target.files[0]);
    };

    const handleSave = async () => {
        try {
            const formDataToSend = new FormData();
            formDataToSend.append('opportunity', new Blob([JSON.stringify(formData)], { type: 'application/json' }));
            if (image) {
                formDataToSend.append('volunteerImage', image);
            }

            const response = await axios.put(`http://localhost:8080/api/volunteer/opportunity/${opportunity.opportunityID}`, formDataToSend, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            const updatedOpportunity = response.data;
            navigate(`/opportunity/${updatedOpportunity.opportunityID}`);
            onClose();
            window.location.reload();
        } catch (error) {
            console.error("Error saving opportunity:", error);
        }
    };

    const handleConfirmSave = () => {
        setIsConfirmDialogOpen(false);
        handleSave();
    };

    return (
        <>
            <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
                <DialogTitle>Update Opportunity</DialogTitle>
                <DialogContent>
                    <form>
                        <Grid container spacing={3}>
                            <Grid item xs={12} mt={2}>
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
                                    error={Boolean(descriptionError)}
                                    helperText={descriptionError || 'Maximum 500 words'}
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
                                    onChange={handleChange}
                                    min={today}
                                    error={Boolean(dateErrors.registrationStartDate)}
                                    helperText={dateErrors.registrationStartDate}
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
                                    onChange={handleChange}
                                    min={formData.registrationStartDate || today}
                                    error={Boolean(dateErrors.registrationEndDate)}
                                    helperText={dateErrors.registrationEndDate}
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
                                    error={Boolean(dateErrors.volunteerDatetime)}
                                    helperText={dateErrors.volunteerDatetime}
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
                    <Button onClick={() => setIsConfirmDialogOpen(true)} color="primary">
                        Save Changes
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Confirmation Dialog */}
            <Dialog open={isConfirmDialogOpen} onClose={() => setIsConfirmDialogOpen(false)}>
                <DialogTitle>Confirm Save</DialogTitle>
                <DialogContent>
                    Are you sure you want to save the changes to this opportunity?
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setIsConfirmDialogOpen(false)} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={handleConfirmSave} color="primary">
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
};

export default UpdateOpportunity;
