import React, { useState, useEffect } from 'react';
import { Button, TextField, Box, Typography, Grid, Container, Snackbar, Dialog, DialogTitle, DialogActions, DialogContent, IconButton, InputAdornment } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const VolunteerSignUp = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [opportunity, setOpportunity] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    address: '',
    phoneNumber: '',
  });
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);

  const fetchOpportunityDetail = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/volunteer/opportunity/${id}`);
      setOpportunity(response.data);
    } catch (error) {
      console.error('Error fetching opportunity details:', error);
    }
  };

  useEffect(() => {
    fetchOpportunityDetail();
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setOpenDialog(true);
  };

  const handleConfirmSubmit = async () => {
    try {
      const dataToSubmit = { ...formData, opportunityId: id };
      await axios.post(`http://localhost:8080/api/volunteer/signup/${id}`, dataToSubmit);
      setSuccessMessage('Successfully signed up for the opportunity!');
      setSnackbarOpen(true);
      setTimeout(() => navigate('/volunteer'), 2000);
    } catch (error) {
      console.error('Error signing up for the opportunity:', error);
    } finally {
      setOpenDialog(false);
    }
  };

  const handleCancelSubmit = () => setOpenDialog(false);

  const handleSnackbarClose = () => setSnackbarOpen(false);

  const handleClickShowPassword = () => setShowPassword(!showPassword);

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom align="center">
        Sign Up for {opportunity?.title || 'the Opportunity'}
      </Typography>

      <form onSubmit={handleSubmit}>
        <TextField
          label="First Name"
          name="firstName"
          value={formData.firstName}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Last Name"
          name="lastName"
          value={formData.lastName}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Email"
          name="email"
          type="email"
          value={formData.email}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Password"
          name="password"
          type={showPassword ? 'text' : 'password'}
          value={formData.password}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
          InputProps={{
            endAdornment: (
              <InputAdornment position="end">
                <IconButton onClick={handleClickShowPassword} edge="end">
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
        <TextField
          label="Address"
          name="address"
          value={formData.address}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <TextField
          label="Phone Number"
          name="phoneNumber"
          value={formData.phoneNumber}
          onChange={handleChange}
          fullWidth
          margin="normal"
          required
        />
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{ paddingX: 4, paddingY: 1.5 }}
          >
            Sign Up
          </Button>
        </Box>
      </form>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
        message={successMessage}
      />

      <Dialog open={openDialog} onClose={handleCancelSubmit}>
        <DialogTitle>Confirm Sign-Up</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to sign up for this opportunity?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancelSubmit} color="secondary">Cancel</Button>
          <Button onClick={handleConfirmSubmit} color="primary">Yes</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default VolunteerSignUp;
