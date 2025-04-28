import React, { useState, useEffect } from 'react';
import {
  Button,
  TextField,
  Container,
  Typography,
  Box,
  Fade,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Stepper,
  Step,
  StepLabel,
  StepConnector,
  styled,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import PetsIcon from '@mui/icons-material/Pets'; // Icon for active steps
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'; // Completed step icon
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked'; // Inactive step icon

// Custom Step Connector
const CustomConnector = styled(StepConnector)(({ theme }) => ({
  '& .MuiStepConnector-line': {
    borderColor: theme.palette.grey[300],
    borderTopWidth: 2,
    borderStyle: 'dotted',
  },
}));

// Custom Step Icon
const CustomStepIcon = styled('div')(({ theme, ownerState }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  backgroundColor: ownerState.active
    ? theme.palette.primary.main
    : theme.palette.grey[300],
  color: ownerState.active ? '#fff' : theme.palette.grey[500],
  width: 40,
  height: 40,
  borderRadius: '50%',
  transition: 'background-color 0.3s ease, color 0.3s ease',
}));

const CreateOpportunity = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    location: '',
    hoursWorked: 0,
    volunteersNeeded: 0,
    registrationStartDate: '',
    registrationEndDate: '',
    volunteerDatetime: '',
  });

  const [imageFile, setImageFile] = useState(null);
  const [imageUrl, setImageUrl] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [descriptionError, setDescriptionError] = useState('');
  const [userId, setUserId] = useState(null);
  const [currentStep, setCurrentStep] = useState(0);
  const [dateErrors, setDateErrors] = useState({
    registrationStartDate: '',
    registrationEndDate: '',
    volunteerDatetime: '',
  });
  const navigate = useNavigate();

  const steps = [
    'Title and Description',
    'Dates',
    'Hours, Volunteers, and Image',
  ];

  useEffect(() => {
    try {
      const storedUser = JSON.parse(localStorage.getItem('user'));
      if (storedUser?.userId) {
        setUserId(storedUser.userId);
      } else {
        setErrorMessage('User is not logged in.');
      }
    } catch (err) {
      setErrorMessage('Invalid user data in localStorage.');
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));

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
    const file = e.target.files[0];
    if (file) {
      setImageFile(file);
      const previewUrl = URL.createObjectURL(file);
      setImageUrl(previewUrl);
    }
  };

  const isStepValid = () => {
    const { title, description, registrationStartDate, registrationEndDate, volunteerDatetime, hoursWorked, volunteersNeeded } = formData;

    if (currentStep === 0) {
      return title && description && !descriptionError;
    }

    if (currentStep === 1) {
      return registrationStartDate && registrationEndDate && volunteerDatetime && !Object.values(dateErrors).some(Boolean);
    }

    if (currentStep === 2) {
      return hoursWorked > 0 && volunteersNeeded > 0 && imageFile;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isStepValid()) {
      setErrorMessage('Please fix the errors before proceeding.');
      return;
    }

    if (currentStep === steps.length - 1) {
      setOpenDialog(true);
    } else {
      setCurrentStep((prevStep) => prevStep + 1);
    }
  };

  const handleConfirm = async () => {
    try {
      setIsSubmitting(true);
      setErrorMessage('');

      const requestData = new FormData();
      Object.entries(formData).forEach(([key, value]) => requestData.append(key, value));
      if (userId) requestData.append('creatorId', userId);
      if (imageFile) requestData.append('volunteerImage', imageFile);

      await axios.post('http://localhost:8080/api/volunteer/opportunity', requestData);

      navigate('/volunteer', { replace: true });
      window.location.reload();
    } catch (error) {
      setErrorMessage(error.response?.data?.message || 'Failed to create opportunity. Please try again.');
    } finally {
      setIsSubmitting(false);
      setOpenDialog(false);
    }
  };

  return (

    <Container
      maxWidth={false} // This disables the default maxWidth behavior
      style={{ maxWidth: '1500px', margin: '0 auto' }} // Adjust width and center the form
    >

      <form onSubmit={handleSubmit} style={{ width: '100%', marginTop: 10 }}>
        {/* Custom Stepper */}
        <Stepper
      alternativeLabel
      activeStep={currentStep}
      connector={<CustomConnector />}
    >
      {steps.map((label, index) => (
        <Step key={index}>
          <StepLabel
            StepIconComponent={(props) => (
              <CustomStepIcon ownerState={props}>
                {props.completed ? (
                  <CheckCircleOutlineIcon />
                ) : props.active ? (
                  <PetsIcon />
                ) : (
                  <RadioButtonUncheckedIcon />
                )}
              </CustomStepIcon>
            )}
          >
            <Typography
              variant="body2"
              style={{
                fontWeight: index === currentStep ? 'bold' : 'normal',
                color: index <= currentStep ? 'black' : '#888',
              }}
            >
              {label}
            </Typography>
          </StepLabel>
        </Step>
      ))}
    </Stepper>

        {currentStep === 0 && (
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                label="Title"
                name="title"
                fullWidth
                value={formData.title}
                onChange={handleChange}
                required
                margin="dense" 
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Description"
                name="description"
                fullWidth
                value={formData.description}
                onChange={handleChange}
                required
                margin="dense" 
                multiline
                rows={4}
                error={Boolean(descriptionError)}
                helperText={descriptionError || 'Maximum 500 words'}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Location"
                name="location"
                fullWidth
                value={formData.location}
                onChange={handleChange}
                required
                margin="dense" 
              />
            </Grid>
          </Grid>
        )}

        {currentStep === 1 && (
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                label="Registration Start Date"
                name="registrationStartDate"
                type="datetime-local"
                fullWidth
                value={formData.registrationStartDate}
                onChange={handleChange}
                required
                margin="normal"
                InputLabelProps={{ shrink: true }}
                inputProps={{ min: new Date().toISOString().slice(0, 16) }}
                error={Boolean(dateErrors.registrationStartDate)}
                helperText={dateErrors.registrationStartDate}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Registration End Date"
                name="registrationEndDate"
                type="datetime-local"
                fullWidth
                value={formData.registrationEndDate}
                onChange={handleChange}
                required
                margin="normal"
                InputLabelProps={{ shrink: true }}
                inputProps={{
                  min: formData.registrationStartDate || new Date().toISOString().slice(0, 16),
                }}
                error={Boolean(dateErrors.registrationEndDate)}
                helperText={dateErrors.registrationEndDate}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Volunteer Date and Time"
                name="volunteerDatetime"
                type="datetime-local"
                fullWidth
                value={formData.volunteerDatetime}
                onChange={handleChange}
                required
                margin="normal"
                InputLabelProps={{ shrink: true }}
                inputProps={{
                  min: formData.registrationEndDate || new Date().toISOString().slice(0, 16),
                }}
                error={Boolean(dateErrors.volunteerDatetime)}
                helperText={dateErrors.volunteerDatetime}
              />
            </Grid>
          </Grid>
        )}

        {currentStep === 2 && (
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField
                label="Hours Worked"
                name="hoursWorked"
                type="number"
                fullWidth
                value={formData.hoursWorked}
                onChange={handleChange}
                required
                margin="normal"
                inputProps={{ min: 1 }}
              />
            </Grid>
            <Grid item xs={6}>
              <TextField
                label="Volunteers Needed"
                name="volunteersNeeded"
                type="number"
                fullWidth
                value={formData.volunteersNeeded}
                onChange={handleChange}
                required
                margin="normal"
                inputProps={{ min: 1 }}
              />
            </Grid>
            <Grid item xs={12}>
              <Box mt={1}>
                {imageUrl && (
                  <img src={imageUrl} alt="Preview" width="100%" style={{ margin: '10px 0' }} />
                )}
                <input type="file" accept="image/*" onChange={handleImageChange} />
              </Box>
            </Grid>
          </Grid>
        )}
        <Box mt={3} display="flex" justifyContent="space-between" alignItems="center">
          {currentStep > 0 && (
            <Button
              variant="outlined"
              color="secondary"
              onClick={() => setCurrentStep((prevStep) => prevStep - 1)}
              style={{ marginRight: 'auto' }} // Ensures it's aligned on the left
            >
              Back
            </Button>
          )}
          <Button
            variant="contained"
            color="primary"
            type="submit"
            disabled={!isStepValid()}
            style={{ marginLeft: 'auto' }} // Ensures it's aligned on the right
          >
            {currentStep === steps.length - 1 ? 'Submit' : 'Next'}
          </Button>
        </Box>


      </form>

      {/* Confirmation Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Confirm Submission</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to create this opportunity?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} color="secondary">
            Cancel
          </Button>
          <Button
            onClick={handleConfirm}
            color="primary"
            disabled={isSubmitting}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {errorMessage && (
        <Box mt={3}>
          <Typography color="error" align="center">
            {errorMessage}
          </Typography>
        </Box>
      )}
    </Container>

  );
};

export default CreateOpportunity;
