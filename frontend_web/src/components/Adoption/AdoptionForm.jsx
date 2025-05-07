import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  TextField,
  Button,
  Snackbar,
  Card,
  CardContent,
  CardMedia,
  Grid,
} from '@mui/material';
import axios from 'axios';
import { useUser } from '../UserContext';

const AdoptionForm = ({ pet }) => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    name: '',
    address: '',
    contactNumber: '',
    adoptionDate: '',
    breed: pet?.breed || '',
    description: pet?.description || '',
    petType: pet?.type || '',
  });

  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (pet) {
      setFormData((prev) => ({
        ...prev,
        breed: pet.breed,
        description: pet.description,
        petType: pet.type,
      }));
    }
    if (user) {
      setFormData((prev) => ({
        ...prev,
        name: `${user.firstName} ${user.lastName}`,
        address: user.address,
        contactNumber: user.phoneNumber,
      }));
    }
  }, [pet, user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'contactNumber' && !/^[0-9]*$/.test(value)) {
      setErrorMessage('Contact number must be numeric');
      return;
    }
    if (name === 'name' && !/^[a-zA-Z\s.]*$/.test(value)) {
      setErrorMessage('Name must only contain letters, spaces, and periods');
      return;
    }
    setErrorMessage('');
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.contactNumber.match(/^[0-9]+$/)) {
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
      status: 'PENDING',
      adoptionID: Date.now(),
      photo: pet?.photo || '',
    };
    resetForm();
    try {
      await axios.post(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`, newAdoption);
      setSuccessMessage('Adoption form submitted successfully!');
    } catch (err) {
      console.error('Submission error:', err);
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      address: '',
      contactNumber: '',
      adoptionDate: '',
      breed: pet?.breed || '',
      description: pet?.description || '',
      petType: pet?.type || '',
    });
  };

  return (
    <Box maxWidth="md" mx="auto" p={3}>
      <Grid container spacing={3} alignItems="flex-start">
        <Grid item xs={12} md={5}>
          <Card sx={{ boxShadow: 3 }}>
            {pet?.photo && (
              <CardMedia
                component="img"
                height="200"
                image={pet.photo}
                alt={`${pet.type} - ${pet.breed}`}
              />
            )}
            <CardContent>
              <Typography variant="h6" color="text.secondary">{pet?.type}</Typography>
              <Typography variant="subtitle1" fontWeight="bold">Breed: {pet?.breed}</Typography>
              <Typography variant="body2" mt={1}>{pet?.description}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={7}>
          <Typography variant="h4" fontWeight="bold" color="#5A20A8" gutterBottom>
            Adoption Form
          </Typography>
          <form onSubmit={handleSubmit}>
            <TextField
              label="Your Name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Address"
              name="address"
              value={formData.address}
              onChange={handleChange}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Contact Number"
              name="contactNumber"
              value={formData.contactNumber}
              onChange={handleChange}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Submission Date"
              type="date"
              name="adoptionDate"
              value={formData.adoptionDate || new Date().toISOString().split('T')[0]}
              onChange={handleChange}
              fullWidth
              margin="normal"
              InputLabelProps={{ shrink: true }}
              inputProps={{
                min: new Date().toISOString().split('T')[0],
                max: new Date().toISOString().split('T')[0],
              }}
            />
            {errorMessage && (
              <Typography color="error" variant="body2" mt={1}>
                {errorMessage}
              </Typography>
            )}
            <Button
              type="submit"
              variant="contained"
              fullWidth
              sx={{ mt: 3, backgroundColor: '#5A20A8', color: '#fff' }}
            >
              Submit Adoption
            </Button>
          </form>
        </Grid>
      </Grid>
      <Snackbar
        open={!!successMessage}
        autoHideDuration={6000}
        onClose={() => setSuccessMessage('')}
        message={successMessage}
        sx={{ '& .MuiSnackbarContent-root': { backgroundColor: '#5A20A8' } }}
      />
    </Box>
  );
};

export default AdoptionForm;