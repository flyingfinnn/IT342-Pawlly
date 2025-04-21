import React, { useState, useEffect } from 'react';
import { TextField, Button, Snackbar, Typography, Box } from '@mui/material';
import axios from 'axios';
import { useUser } from '../UserContext';

const AdoptionForm = ({ pet }) => {
    const { user } = useUser();
    const [formData, setFormData] = useState({
        name: '',
        address: '',
        contactNumber: '',
        adoptionDate: '',
        breed: pet ? pet.breed : '',
        description: pet ? pet.description : '', 
        petType: pet ? pet.type : '',
    });    
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        if (pet) {
            setFormData((prevData) => ({
                ...prevData,
                breed: pet.breed,
                description: pet.description,
                petType: pet.type,
            }));
        }

        if (user) {
            setFormData((prevData) => ({
                ...prevData,
                name: user.firstName + ' ' + user.lastName,
                address: user.address,
                contactNumber: user.phoneNumber,
            }));
        }
    }, [pet, user]);

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === 'contactNumber' && !/^\d*$/.test(value)) {
            setErrorMessage('Contact number must be numeric');
            return;
        }

        if (name === 'name' && !/^[a-zA-Z\s.]*$/.test(value)) {
            setErrorMessage('Name must only contain letters, spaces, and periods');
            return;
        }

        setErrorMessage('');
        setFormData ((prevData) => ({ ...formData, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.contactNumber.match(/^\d+$/)) {
            setErrorMessage('Contact number must be numeric');
            return;
        }

        if (!formData.name.match(/^[a-zA-Z\s.]+$/)) {
            setErrorMessage('Name must only contain letters, spaces, and periods');
            return;
        }

        const newAdoption = {
            ...formData,
            adoptionDate: formData.adoptionDate || new Date().toISOString().split('T')[0],
            description: pet ? pet.description : formData.description,
            status: 'PENDING',
            adoptionID: Date.now(),
            photo: pet ? pet.photo : '',
        };

        resetForm();

        try {
            await axios.post('http://localhost:8080/api/adoptions', newAdoption);
            setSuccessMessage('Adoption form submitted successfully!');
        } catch (error) {
            console.error('Failed to submit the adoption form:', error);
        }
    };

    const resetForm = () => {
        setFormData({
            name: '',
            address: '',
            contactNumber: '',
            adoptionDate: '',
            breed: pet ? pet.breed : '',
            petDescription: pet ? pet.description : '',
            petType: pet ? pet.type : ''
        });
    };

    return (
        <div style={styles.container}>
            <form onSubmit={handleSubmit} style={styles.form}>
                <Box 
                    sx={{
                        border: '2px solid #5A20A8',
                        borderRadius: '10px',
                        padding: '20px', 
                        boxShadow: '0px 4px 10px rgba(90, 32, 168, 0.2)', 
                    }}
                >
                    <div style={styles.leftColumn}>
                        {pet && pet.photo ? ( 
                            <Box sx={{ textAlign: 'center', marginBottom: 2 }}>
                                <img 
                                    src={pet.photo} 
                                    alt={`${pet.type} - ${pet.breed}`} 
                                    style={{ 
                                        maxHeight: '200px', 
                                        maxWidth: '100%', 
                                        borderRadius: '10px', 
                                        boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.2)' 
                                    }} 
                                />
                            </Box>
                        ) : (
                            <Box sx={{ textAlign: 'center', marginBottom: 2 }}>
                                <img 
                                    src="path/to/placeholder/image.jpg" 
                                    alt="No Image Available" 
                                    style={{ 
                                        maxHeight: '200px', 
                                        maxWidth: '100%', 
                                        borderRadius: '10px', 
                                        boxShadow: '0px 4px 10px rgba(0, 0, 0, 0.2)' 
                                    }} 
                                />
                            </Box>
                        )}
                        <TextField
                            label="Pet Type"
                            name="type"
                            value={formData.petType}
                            variant="outlined"
                            fullWidth
                            disabled
                            sx={{ marginBottom: 2 }}
                        />
                        <TextField
                            label="Breed of Pet"
                            name="breed"
                            value={formData.breed}
                            variant="outlined"
                            fullWidth
                            disabled
                            sx={{ marginBottom: 2 }}
                        />
                        <TextField
                            label="Pet Description"
                            name="description" 
                            value={formData.description}
                            variant="outlined"
                            fullWidth
                            disabled
                            sx={{ marginBottom: 2 }}
                        />
                    </div>
                </Box>
                <div style={styles.rightColumn}>
                    <Typography
                        variant="h4"
                        sx={{
                            color: '#5A20A8',
                            marginBottom: 2,
                            fontFamily: "'Caramel', sans-serif",
                            fontWeight: "bold",
                        }}
                    >
                        Adoption Form
                    </Typography>
                    <TextField
                            label="Your Name"
                            name="name"
                            value={formData.name}
                            variant="outlined"
                            fullWidth
                            onChange={handleChange}
                            sx={{ marginBottom: 2 }}
                        />
                        <TextField
                            label="Address"
                            name="address"
                            value={formData.address}
                            variant="outlined"
                            fullWidth
                            onChange={handleChange}
                            sx={{ marginBottom: 2 }}
                        />
                        <TextField
                            label="Contact Number"
                            name="contactNumber"
                            value={formData.contactNumber}
                            variant="outlined"
                            fullWidth
                            onChange={handleChange}
                            sx={{ marginBottom: 2 }}
                        />
                    <TextField
                        label="Submission Date"
                        type="date"
                        name="submissionDate"
                        value={formData.adoptionDate || new Date().toISOString().split('T')[0]} 
                        onChange={handleChange}
                        InputLabelProps={{ shrink: true }}
                        inputProps={{
                            min: new Date().toISOString().split('T')[0], // today's date as the minimum
                            max: new Date().toISOString().split('T')[0]  // today's date as the maximum
                        }}
                        variant="outlined"
                        required
                        fullWidth
                        sx={{ marginBottom: 2 }}
                    />
                </div>
            </form>

            <div style={styles.errorContainer}>
                {errorMessage && (
                    <Typography variant="body2" color="error">
                        {errorMessage}
                    </Typography>
                )}
            </div>

            <Button
                variant="contained"
                type="submit"
                sx={{
                    backgroundColor: '#5A20A8',
                    color: 'white',
                    marginTop: '20px',
                }}
                onClick={handleSubmit}
            >
                Submit Adoption
            </Button>

            <Snackbar
                open={!!successMessage}
                autoHideDuration={6000}
                onClose={() => setSuccessMessage('')}
                message={successMessage}
                sx={{
                    '& .MuiSnackbarContent-root': {
                        backgroundColor: '#5A20A8',
                        color: 'white'
                    }
                }}
            />
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '20px',
        margin: '0 auto',
        maxWidth: '800px',
    },
    form: {
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '20px',
        width: '100%',
    },
    leftColumn: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
    },
    leftColumnBorder: {
        border: '2px solid #5A20A8',
        borderRadius: '10px',
        padding: '20px',
        boxShadow: '0px 4px 10px rgba(90, 32, 168, 0.2)', 
    },
    rightColumn: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'flex-start',
    },
    errorContainer: {
        height: '24px', 
        textAlign: 'left',
        marginTop: '10px',
    },
};

export default AdoptionForm;
