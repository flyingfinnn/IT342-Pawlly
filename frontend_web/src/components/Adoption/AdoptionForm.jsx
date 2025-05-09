import React, { useState, useEffect } from 'react';
import {
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  TextField,
  Button,
  Snackbar,
} from '@mui/material';
import axios from 'axios';
import { useUser } from '../UserContext';

const ModernAdoptionForm = ({ pet }) => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    userId: user?.userId || '',
    petId: pet?.id || '',
    householdType: '',
    householdOwnership: '',
    numAdults: '',
    numChildren: '',
    otherPets: false,
    experienceWithPets: '',
    dailyRoutine: '',
    hoursAlonePerDay: '',
    reasonForAdoption: '',
    petName: pet?.name || '',
    status: 'pending'
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
    if (name === 'contactNumber' && !/^\d*$/.test(value)) {
      setErrorMessage('Contact number must be numeric');
      return;
    }
    if (name === 'name' && !/^[a-zA-Z\s.]*$/.test(value)) {
      setErrorMessage('Name must only contain letters, spaces, and periods');
      return;
    }
    setErrorMessage('');
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const newAdoption = {
      ...formData,
      userId: user.userId,
      petId: pet.id,
      petName: pet.name,
      status: 'pending',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };

    try {
      await axios.post(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`, newAdoption);
      setSuccessMessage('Adoption form submitted successfully!');
      resetForm();
    } catch (error) {
      console.error('Failed to submit the adoption form:', error);
      setErrorMessage('Failed to submit adoption form. Please try again.');
    }
  };

  const resetForm = () => {
    setFormData({
      userId: user?.userId || '',
      petId: pet?.id || '',
      householdType: '',
      householdOwnership: '',
      numAdults: '',
      numChildren: '',
      otherPets: false,
      experienceWithPets: '',
      dailyRoutine: '',
      hoursAlonePerDay: '',
      reasonForAdoption: '',
      petName: pet?.name || '',
      status: 'pending'
    });
  };

  return (
    <Box maxWidth="900px" mx="auto" p={4}>
      <form onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 2, borderRadius: 3, boxShadow: 3 }}>
              <CardContent>
                {pet?.photo && (
                  <Box textAlign="center" mb={2}>
                    <img
                      src={pet.photo}
                      alt={`${pet.name}`}
                      style={{ maxHeight: '200px', maxWidth: '100%', borderRadius: 10 }}
                    />
                  </Box>
                )}
                <Typography variant="h5" color="primary" fontWeight="bold" gutterBottom>
                  {pet?.name || 'Unnamed Pet'}
                </Typography>
                <Typography variant="body1"><strong>Type:</strong> {pet?.type}</Typography>
                <Typography variant="body1"><strong>Breed:</strong> {pet?.breed}</Typography>
                <Typography variant="body1"><strong>Age:</strong> {pet?.age}</Typography>
                <Typography variant="body1"><strong>Gender:</strong> {pet?.gender}</Typography>
                <Typography variant="body1" mt={1}><strong>Description:</strong> {pet?.description}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Typography variant="h4" color="primary" fontWeight="bold" gutterBottom>
              Adoption Form
            </Typography>
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
              <Typography color="error" mt={1}>
                {errorMessage}
              </Typography>
            )}
            <Button
              type="submit"
              variant="contained"
              sx={{ mt: 3, backgroundColor: '#5A20A8' }}
              fullWidth
            >
              Submit Adoption
            </Button>
          </Grid>
        </Grid>
      </form>
      <Snackbar
        open={!!successMessage}
        autoHideDuration={6000}
        onClose={() => setSuccessMessage('')}
        message={successMessage}
        sx={{ '& .MuiSnackbarContent-root': { backgroundColor: '#5A20A8', color: 'white' } }}
      />
    </Box>
  );
};

export default ModernAdoptionForm;
