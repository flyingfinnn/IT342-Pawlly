import React, { useState, useEffect } from 'react';
import {
  Button,
  TextField,
  Container,
  Typography,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Stepper,
  Step,
  StepLabel,
  styled,
} from '@mui/material';
import StepConnector, { stepConnectorClasses } from '@mui/material/StepConnector';
import PetsIcon from '@mui/icons-material/Pets'; // Icon for active steps
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'; // Completed step icon
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked'; // Inactive step icon
import AddIcon from '@mui/icons-material/Add';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// Import your custom connector images
import NotPassedLine from '../../assets/notpassed_stepper.png';
import PassedLine from '../../assets/passed_stepper.png';
import CurrentLine from '../../assets/current_stepper.png';

const CustomConnector = styled(StepConnector)(({ theme, activeStep }) => ({
  [`&.${stepConnectorClasses.alternativeLabel}`]: {
    top: 12, // Adjust vertical alignment of connector
    margin: 0, // No margin
    padding: 0, // No padding
  },
  [`& .${stepConnectorClasses.line}`]: {
    height: 20, // Increased height of the connector line
    border: 'none', // No border
    marginTop: 5, // No extra margin
    padding: 0, // No extra padding
    position: 'absolute',
    top: '50%', // Keep vertical alignment centered
    transform: 'translateY(-50%)', // Adjust vertical alignment precisely
    width: '100%', // Line stretches fully between icons
    backgroundSize: '150% 150%', // Enlarges the background image
    backgroundRepeat: 'no-repeat',
    backgroundPosition: 'center',
    backgroundImage: `url(${NotPassedLine})`, // Default line image for steps that are not active or completed
  },
  [`&.${stepConnectorClasses.active} .${stepConnectorClasses.line}`]: {
    backgroundImage: `url(${CurrentLine})`, // Active step line on the right side
  },
  [`&.${stepConnectorClasses.completed} .${stepConnectorClasses.line}`]: {
    backgroundImage: `url(${PassedLine})`, // Completed step line
  },
}));



// Custom Step Icon styled to appear inline with connector
const CustomStepIconRoot = styled('div')(({ theme, ownerState }) => ({
  display: 'flex',
  flexDirection: 'row', // Inline layout for icon and connector
  alignItems: 'center', // Vertically align icon and connector
  justifyContent: 'center', // Center the content horizontally
  position: 'relative',
  zIndex: 1,
  margin: 0, // Remove any default margin
  padding: 0, // Remove any default padding
  '& .MuiSvgIcon-root': {
    width: 36, // Increased icon size
    height: 36, // Increased icon size
    margin: 0, // Ensure no margin around the icon
    padding: 0, // Ensure no padding inside the icon
    color: ownerState.active
      ? theme.palette.primary.main
      : ownerState.completed
        ? 'rgb(103, 91, 200)'
        : theme.palette.grey[500], // Icon color based on state
  },
}));

const CustomStepIcon = (props) => {
  const { active, completed, className } = props;

  return (
    <CustomStepIconRoot ownerState={{ active, completed }} className={className}>
      {completed ? (
        <CheckCircleOutlineIcon />
      ) : active ? (
        <PetsIcon />
      ) : (
        <RadioButtonUncheckedIcon />
      )}
    </CustomStepIconRoot>
  );
};


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
    'Location',
    'Upload Images'
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

    if (
      ['registrationStartDate', 'registrationEndDate', 'volunteerDatetime'].includes(
        name
      )
    ) {
      const errors = { ...dateErrors };

      if (name === 'registrationStartDate' && value) {
        errors.registrationStartDate = '';
        if (
          formData.registrationEndDate &&
          new Date(value) >= new Date(formData.registrationEndDate)
        ) {
          errors.registrationEndDate = 'Start date must be before the end date.';
        }
      }

      if (name === 'registrationEndDate' && value) {
        errors.registrationEndDate = '';
        if (new Date(value) <= new Date(formData.registrationStartDate)) {
          errors.registrationEndDate = 'End date must be after the start date.';
        }
        if (
          formData.volunteerDatetime &&
          new Date(value) >= new Date(formData.volunteerDatetime)
        ) {
          errors.volunteerDatetime =
            'End date must be before the volunteer date.';
        }
      }

      if (name === 'volunteerDatetime' && value) {
        errors.volunteerDatetime = '';
        if (new Date(value) <= new Date(formData.registrationEndDate)) {
          errors.volunteerDatetime =
            'Volunteer date must be after the registration end date.';
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
    const {
      title,
      description,
      registrationStartDate,
      registrationEndDate,
      volunteerDatetime,
      location,
      hoursWorked,
      volunteersNeeded,
      imageUrl,  // Image URL for the image upload step
    } = formData;

    if (currentStep === 0) {
      // Title and Description validation
      return title && description && !descriptionError;
    }

    if (currentStep === 1) {
      // Date fields validation
      return (
        registrationStartDate &&
        registrationEndDate &&
        volunteerDatetime &&
        !Object.values(dateErrors).some(Boolean)
      );
    }

    if (currentStep === 2) {
      // Location, Hours Worked, and Volunteers Needed validation
      return (
        location &&
        hoursWorked > 0 &&
        volunteersNeeded > 0
      );
    }

    if (currentStep === 3) {
      // Image upload validation
      return imageUrl !== null;
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
      Object.entries(formData).forEach(([key, value]) =>
        requestData.append(key, value)
      );
      if (userId) requestData.append('creatorId', userId);
      if (imageFile) requestData.append('volunteerImage', imageFile);

      await axios.post('http://localhost:8080/api/volunteer/opportunity', requestData);

      navigate('/volunteer', { replace: true });
      window.location.reload();
    } catch (error) {
      setErrorMessage(
        error.response?.data?.message ||
        'Failed to create opportunity. Please try again.'
      );
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
          activeStep={currentStep}
          alternativeLabel
          connector={<CustomConnector />}
          style={{ overflow: 'visible' }} // Prevent clipping
        >
          {steps.map((label, index) => (
            <Step key={label}>
              <StepLabel StepIconComponent={CustomStepIcon}>
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
            <Grid item xs={12} mt={2}>
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
          </Grid>
        )}

        {currentStep === 1 && (
          <Grid container spacing={2}>
            <Grid item xs={6} mt={5.8}>
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
            <Grid item xs={6} mt={5.8}>
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
                  min:
                    formData.registrationStartDate ||
                    new Date().toISOString().slice(0, 16),
                }}
                error={Boolean(dateErrors.registrationEndDate)}
                helperText={dateErrors.registrationEndDate}
              />
            </Grid>
            <Grid item xs={12} mt={2} mb={2.9}>
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
                  min:
                    formData.registrationEndDate ||
                    new Date().toISOString().slice(0, 16),
                }}
                error={Boolean(dateErrors.volunteerDatetime)}
                helperText={dateErrors.volunteerDatetime}
              />
            </Grid>
          </Grid>
        )}

        {currentStep === 2 && (
          <Grid container spacing={2}>
            {/* Location - separate */}
            <Grid item xs={12} mt={5.5}>
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

            {/* Hours Worked and Volunteers Needed - inline */}
            <Grid item xs={6} mt={3}>
              <TextField
                label="Hours Worked"
                name="hoursWorked"
                type="number"
                fullWidth
                value={formData.hoursWorked}
                onChange={handleChange}
                required
                margin="dense"
                inputProps={{ min: 0 }}  // Prevent negative values
              />
            </Grid>
            <Grid item xs={6} mt={3} mb={5.3}>
              <TextField
                label="Volunteers Needed"
                name="volunteersNeeded"
                type="number"
                fullWidth
                value={formData.volunteersNeeded}
                onChange={handleChange}
                required
                margin="dense"
                inputProps={{ min: 0 }}  // Prevent negative values
              />
            </Grid>
          </Grid>
        )}
        {currentStep === 3 && (
          <Grid container spacing={2}>
            <Grid item xs={12} mt={3.25} sx={{ position: 'relative', height: 250 }}>
              {/* Display the upload prompt if no image is uploaded */}
              {!imageUrl && (
                <>
                  <Box
                    sx={{
                      border: '2px dashed #888',  // Dashed border for outline
                      height: '100%',
                      display: 'flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      position: 'relative',
                      cursor: 'pointer',
                      backgroundColor: '#f9f9f9',  // Light background
                    }}
                  >
                    <AddIcon sx={{ color: '#888', fontSize: 40 }} />
                    <Typography variant="body2" sx={{ position: 'absolute', color: '#888', bottom: 10 }}>
                      Click to upload or drag and drop
                    </Typography>
                    <input
                      type="file"
                      accept="image/*"
                      style={{
                        opacity: 0,
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                        cursor: 'pointer',
                      }}
                      onChange={handleImageChange}
                    />
                  </Box>
                </>
              )}

              {/* Display the image preview if an image is uploaded */}
              {imageUrl && (
                <img
                  src={imageUrl}
                  alt="Preview"
                  style={{ width: '100%', maxHeight: 200, objectFit: 'cover' }}
                />
              )}
            </Grid>
          </Grid>
        )}



        {/* Navigation Buttons */}
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
          <Button onClick={handleConfirm} color="primary" disabled={isSubmitting}>
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
