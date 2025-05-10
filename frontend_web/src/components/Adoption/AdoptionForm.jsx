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
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import axios from 'axios';
import { useUser } from '../UserContext';

const ModernAdoptionForm = ({ pet }) => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    user_id: user ? user.userId : '',
    pet_id: pet?.pid || '',
    pet_name: pet?.name || '',
    household_type: '',
    household_ownership: '',
    num_adults: '',
    num_children: '',
    other_pets: false,
    experience_with_pets: '',
    daily_routine: '',
    hours_alone_per_day: '',
    reason_for_adoption: '',
    status: 'PENDING',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString(),
    accept_or_reject: '',
  });
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (pet) {
      setFormData((prev) => ({
        ...prev,
        pet_id: pet.pid,
        pet_name: pet.name,
      }));
    }
    if (user) {
      setFormData((prev) => ({
        ...prev,
        user_id: user.userId,
      }));
    }
  }, [pet, user]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Add any validation as needed
    try {
      await axios.post(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`, formData);
      setSuccessMessage('Adoption application submitted successfully!');
      resetForm();
    } catch (error) {
      setErrorMessage('Failed to submit the adoption application.');
    }
  };

  const resetForm = () => {
    setFormData({
      user_id: user ? user.userId : '',
      pet_id: pet?.pid || '',
      pet_name: pet?.name || '',
      household_type: '',
      household_ownership: '',
      num_adults: '',
      num_children: '',
      other_pets: false,
      experience_with_pets: '',
      daily_routine: '',
      hours_alone_per_day: '',
      reason_for_adoption: '',
      status: 'PENDING',
      created_at: new Date().toISOString(),
      updated_at: new Date().toISOString(),
      accept_or_reject: '',
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
              Adoption Application
            </Typography>
            <TextField
              label="Household Type"
              name="household_type"
              value={formData.household_type}
              onChange={handleChange}
              fullWidth
              margin="normal"
            />
            <FormControl fullWidth margin="normal">
              <InputLabel>Household Ownership</InputLabel>
              <Select
                name="household_ownership"
                value={formData.household_ownership}
                onChange={handleChange}
                required
                label="Household Ownership"
              >
                <MenuItem value="Own">Own</MenuItem>
                <MenuItem value="Rent">Rent</MenuItem>
              </Select>
            </FormControl>
            <TextField
              label="Number of Adults"
              name="num_adults"
              value={formData.num_adults}
              onChange={handleChange}
              type="number"
              fullWidth
              margin="normal"
            />
            <TextField
              label="Number of Children"
              name="num_children"
              value={formData.num_children}
              onChange={handleChange}
              type="number"
              fullWidth
              margin="normal"
            />
            <Box display="flex" alignItems="center" mt={2} mb={2}>
              <label style={{ marginRight: 8 }}>Other Pets:</label>
              <input
                type="checkbox"
                name="other_pets"
                checked={formData.other_pets}
                onChange={handleChange}
              />
            </Box>
            <TextField
              label="Experience with Pets"
              name="experience_with_pets"
              value={formData.experience_with_pets}
              onChange={handleChange}
              fullWidth
              margin="normal"
              multiline
              rows={2}
            />
            <TextField
              label="Daily Routine"
              name="daily_routine"
              value={formData.daily_routine}
              onChange={handleChange}
              fullWidth
              margin="normal"
              multiline
              rows={2}
            />
            <TextField
              label="Hours Alone Per Day"
              name="hours_alone_per_day"
              value={formData.hours_alone_per_day}
              onChange={handleChange}
              type="number"
              fullWidth
              margin="normal"
            />
            <TextField
              label="Reason for Adoption"
              name="reason_for_adoption"
              value={formData.reason_for_adoption}
              onChange={handleChange}
              fullWidth
              margin="normal"
              multiline
              rows={2}
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
              Submit Application
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