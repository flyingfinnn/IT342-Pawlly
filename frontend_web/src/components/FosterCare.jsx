import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Card,
  CardMedia,
  CardContent,
  Grid,
  Modal,
  Box,
  TextField,
} from '@mui/material';
import { useNavigate } from 'react-router-dom'; // Import useNavigate from react-router-dom
import axios from 'axios';

const FosterCare = () => {
  const navigate = useNavigate(); // Initialize useNavigate hook
  const [open, setOpen] = useState(false); // State to manage modal visibility

  // State to manage form inputs
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    address: '',
    phoneNumber: '',
  });

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  // Handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default form submission behavior

    try {
        const response = await axios.post('http://localhost:8080/api/volunteer/signup', formData);
        console.log(response.data);
        alert('Registration successful!'); // Handle successful registration
        setFormData({ firstName: '', lastName: '', email: '', password: '', address: '', phoneNumber: '' }); // Reset form
        handleClose(); // Close modal
    } catch (error) {
        console.error('Error registering for the event', error);
        if (error.response) {
            console.log("Error data:", error.response.data); // Logs server response if available
            alert(`Failed to register: ${error.response.data.message || "Please try again."}`);
        } else {
            alert('Failed to register. Please try again.'); // Handle error if no response from server
        }
    }
};

  // Inline styles
  const styles = {
    container: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
      // Removed backgroundColor property to eliminate the gray background
    },
    content: {
      backgroundColor: '#fff',
      padding: '20px',
      borderRadius: '8px',
      boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
      maxWidth: '800px',
      width: '100%',
    },
    image: {
      height: '300px',
      objectFit: 'cover',
    },
    modalStyle: {
      position: 'absolute',
      top: '50%',
      left: '50%',
      transform: 'translate(-50%, -50%)',
      width: 400,
      bgcolor: 'background.paper',
      boxShadow: 24,
      p: 4,
      borderRadius: '8px',
    },
    backButton: {
      marginBottom: '20px',
      alignItems: 'center',
      // Add additional styles for the back button if needed
    },
  };

  return (
    <Container style={styles.container}>
      {/* Back Button */}
      <Button 
        variant="outlined" 
        color="secondary" 
        style={styles.backButton} 
        onClick={() => navigate('/volunteer')} // Navigate to the volunteer page
      >
        Back
      </Button>
      
      <div style={styles.content}>
        <Grid container spacing={2}>
          <Grid item md={6}>
            <Card>
              <CardMedia
                component="img"
                alt="Foster Care Program"
                image="/images/petimg.png" // Path to your image
                style={styles.image}
              />
              <CardContent>
                <Typography variant="h5" gutterBottom>
                  Become a Foster Care Volunteer
                </Typography>
                <Typography variant="body1" gutterBottom>
                  Our Foster Care Program provides temporary care for animals in need until they can be placed in their forever homes. As a foster caregiver, you play a crucial role in helping animals feel safe and loved.
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item md={6}>
            <Typography variant="h5" gutterBottom>
              Requirements:
            </Typography>
            <ul>
              <li>Must be at least 18 years old.</li>
              <li>Complete a Foster Care Application.</li>
              <li>Provide a safe and loving environment.</li>
              <li>Attend a brief orientation session.</li>
            </ul>

            <Typography variant="h5" gutterBottom>
              Benefits:
            </Typography>
            <ul>
              <li>Make a difference in an animal's life.</li>
              <li>Receive support and resources from our team.</li>
              <li>All necessary supplies provided (food, toys, etc.).</li>
              <li>First-hand experience in caring for animals.</li>
            </ul>

            <Button variant="contained" color="primary" size="large" onClick={handleOpen} style={{ marginTop: '20px' }}>
              Apply to Foster
            </Button>
          </Grid>
        </Grid>

        <footer style={{ textAlign: 'center', marginTop: '20px' }}>
          <Typography variant="body2">Contact us at [insert contact info]</Typography>
        </footer>
      </div>

      {/* Modal for Volunteer Sign Up */}
      <Modal open={open} onClose={handleClose}>
        <Box sx={styles.modalStyle}>
          <Typography variant="h6" component="h2" gutterBottom>
            Volunteer Sign-Up
          </Typography>
          {/* Form Fields */}
          <form onSubmit={handleSubmit}>
            <TextField label="First Name" fullWidth margin="normal" required name="firstName" value={formData.firstName} onChange={handleChange} />
            <TextField label="Last Name" fullWidth margin="normal" required name="lastName" value={formData.lastName} onChange={handleChange} />
            <TextField label="Email" type="email" fullWidth margin="normal" required name="email" value={formData.email} onChange={handleChange} />
            <TextField label="Password" type="password" fullWidth margin="normal" required name="password" value={formData.password} onChange={handleChange} />
            <TextField label="Address" fullWidth margin="normal" required name="address" value={formData.address} onChange={handleChange} />
            <TextField label="Phone Number" type="tel" fullWidth margin="normal" required name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} />

            <Box display="flex" justifyContent="space-between" marginTop="20px">
              <Button variant="outlined" color="secondary" onClick={handleClose}>
                Cancel
              </Button>
              <Button variant="contained" color="primary" type="submit">
                Submit Application
              </Button>
            </Box>
          </form>
        </Box>
      </Modal>
    </Container>
  );
};

export default FosterCare;
