import React, { useEffect, useState } from 'react';
import {
  Button,
  Container,
  Grid,
  Typography,
  Card,
  CardContent,
  CardActions,
  CardMedia,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import CloseIcon from '@mui/icons-material/Close'; // Close Icon
import { motion } from 'framer-motion'; // Importing motion
import './Volunteer.css';
import CreateOpportunity from './CreateOpportunity';
import AuthModal from '../AuthModal';

const Volunteer = () => {
  const [volunteerOpportunities, setVolunteerOpportunities] = useState([]);
  const [visibleOpportunities, setVisibleOpportunities] = useState(4); // Default: Show 4 cards initially
  const [isExpanded, setIsExpanded] = useState(false); // Track Show More / Show Less state
  const [openFormDialog, setOpenFormDialog] = useState(false);
  const [isBlurred, setIsBlurred] = useState(false); // New state for blur effect
  const navigate = useNavigate();
  const [authModalOpen, setAuthModalOpen] = useState(false);

  // Function to handle dialog open
  const handleOpenFormDialog = () => {
    setIsBlurred(true); // Set blur effect when dialog is opened
    setOpenFormDialog(true);
  };

  // Function to handle dialog close
  const handleCloseFormDialog = () => {
    setIsBlurred(false); // Remove blur effect when dialog is closed
    setOpenFormDialog(false);

    // Fetch the userID from localStorage
    const userId = JSON.parse(localStorage.getItem('user'));
    console.log('User ID:', userId);
  };

  // Function to handle form submission success
  const handleFormSubmitSuccess = () => {
    setOpenFormDialog(false); // Close the dialog
    navigate('/volunteer', { replace: true }); // Navigate to the volunteer page
  };

  // FAQ data
  const faqData = [
    {
      question: 'What is required to become a volunteer?',
      answer: 'All volunteers must first submit the application form, deposit 1000 (which will be returned after rendering at least 8 hours of volunteer work per month for 6 consecutive months) and attend the orientation.',
    },
    {
      question: 'Can I choose the volunteer opportunities I want to participate in?',
      answer: 'Yes, volunteers have the flexibility to choose from a variety of opportunities listed on our website. You can select the ones that align with your interests and availability.',
    },
    {
      question: 'Do I need prior experience to volunteer?',
      answer: 'No prior experience is necessary for most volunteer opportunities. Training and orientation are provided to ensure that all volunteers are equipped to help effectively.',
    },
    {
      question: 'Can I volunteer with a friend or group?',
      answer: 'Absolutely! We encourage group volunteering, and you can sign up together. Just make sure each individual fills out a separate registration form.',
    },
    {
      question: 'How can I track my volunteer hours?',
      answer: 'We track volunteer hours through our online system. Volunteers can log their hours directly through their profiles, or you can report your hours manually to the volunteer coordinator.',
    }
  ];

  // Fetch volunteer opportunities from the backend
  useEffect(() => {
    const fetchVolunteerOpportunities = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/volunteer/opportunities');
        console.log(response.data);  // Log the response to check the structure and values
        setVolunteerOpportunities(response.data);
      } catch (error) {
        console.error('Error fetching volunteer opportunities:', error);
        setVolunteerOpportunities([]); // Optionally handle fallback state
        alert('Failed to load volunteer opportunities. Please try again later.'); // Add user-friendly feedback
      }
    };
    fetchVolunteerOpportunities();
  }, []);

  // Function to show more or less opportunities
  const handleShowMoreLess = () => {
    if (isExpanded) {
      setVisibleOpportunities(4); // Show 4 opportunities when collapsed
    } else {
      setVisibleOpportunities(volunteerOpportunities.length); // Show all opportunities when expanded
    }
    setIsExpanded((prev) => !prev); // Toggle the state of expanded
  };

  // Handle "Book" button click
  const handleBookButtonClick = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      // If no token found, open the AuthModal
      setAuthModalOpen(true);
    } else {
      // Proceed to booking functionality or form dialog
      handleOpenFormDialog();
    }
  };


  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 1 }}
    >
      {/* Header Section */}
      <Container maxWidth="lg" sx={{ paddingTop: '4rem', paddingBottom: '1rem', filter: isBlurred ? 'blur(5px)' : 'none' }}>
        <Grid container spacing={4} alignItems="flex-start">
          <Grid item xs={12} md={6}>
            <motion.img
              src="petimg.png"
              alt="Volunteer"
              className="img-fluid rounded"
              style={{ width: '100%', borderRadius: '8px' }}
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.2, duration: 1 }}
            />
          </Grid>
          <Grid
            item
            xs={12}
            md={6}
            sx={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'flex-start',
            }}
          >
            <Typography
              variant="h4"
              gutterBottom
              sx={{
                fontFamily: '"Roboto", "Arial", sans-serif', // Change font family to something modern
                fontWeight: 700, // Make the text bold
                fontSize: '2.5rem', // Increase the font size
                textAlign: 'left', // Center align the text
                letterSpacing: '0.5px', // Add some spacing between letters for a clean look
                color: 'primary.main', // Change text color to the primary theme color
              }}
            >
              Volunteer and Make a Difference for Pets
            </Typography>

            <Typography variant="body1" paragraph>
              Volunteering with pets is a rewarding way to give back and improve the lives of animals in need. From walking dogs at shelters to fostering pets, every moment you spend with them helps build a stronger bond between humans and animals. Whether you're offering companionship, care, or helping them find their forever home, your involvement makes a real difference. Join us in supporting pets who need love, care, and a second chance at life.
            </Typography>
            <Button
              variant="outlined"
              color="secondary"
              size="small"
              sx={{
                padding: '6px 16px',
                fontSize: '0.875rem',
                borderRadius: '18px',
                boxShadow: 'none',
                '&:hover': {
                  color: 'white',
                  backgroundColor: '#675bc8',
                  borderColor: '#675bc8',
                },
                width: 'fit-content',
                display: 'inline-flex',
                alignItems: 'center',
                marginTop: '1rem',
              }}
              onClick={handleBookButtonClick} // Call the updated function
            >
              Book
            </Button>
            {/* Auth Modal */}
            <AuthModal
              open={authModalOpen}
              handleClose={() => setAuthModalOpen(false)} // Close the modal when needed
            />
          </Grid>
        </Grid>
      </Container>

      {/* FAQ Section */}
      <Container maxWidth="md" sx={{ padding: '2rem 0', filter: isBlurred ? 'blur(5px)' : 'none' }}>
        <Typography
          variant="h4"
          gutterBottom
          sx={{
            fontFamily: '"Roboto", "Arial", sans-serif', // Change font family to something modern
            fontWeight: 700, // Make the text bold
            fontSize: '2.2rem', // Increase the font size
            textAlign: 'center', // Center align the text
            letterSpacing: '0.4px', // Add some spacing between letters for a clean look
            color: 'primary.main', // Change text color to the primary theme color
            marginBottom: 4,
          }}
        >
          FAQ's for Volunteering
        </Typography>

        {faqData.map((item, index) => (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.2, duration: 0.6 }}
            key={index}
          >
            <Accordion
              style={{
                marginBottom: '12px', // Reduced margin
                borderRadius: '8px',
                boxShadow: '0 2px 6px rgba(0, 0, 0, 0.1)', // Smaller shadow
              }}
            >
              <AccordionSummary
                expandIcon={<ExpandMoreIcon sx={{ color: 'purple' }} />}
                aria-controls={`panel${index}-content`}
                id={`panel${index}-header`}
                sx={{
                  backgroundColor: 'white',
                  borderRadius: '8px',
                  padding: '5px 18px', // Reduced padding
                  '&:hover': { backgroundColor: '#f5f5f5' },
                }}
              >
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 550,
                    fontSize: '1rem', // Fix the font size so it doesn't change when expanded
                    transition: 'font-size 0.3s', // Smooth transition for font size
                  }}
                >
                  {item.question}
                </Typography>
              </AccordionSummary>

              <AccordionDetails sx={{ padding: '12px 20px', backgroundColor: '#fafafa' }}>
                <Typography variant="body1" sx={{ color: 'primary.main' }}>
                  {item.answer}
                </Typography>
              </AccordionDetails>
            </Accordion>
          </motion.div>
        ))}
      </Container>

      {/* Volunteer Opportunities Section */}
      <Container maxWidth="lg" sx={{ padding: '1rem 0', filter: isBlurred ? 'blur(5px)' : 'none' }}>
        <Typography
          variant="h4"
          gutterBottom
          sx={{
            fontFamily: '"Roboto", "Arial", sans-serif', // Change font family to something modern
            fontWeight: 700, // Make the text bold
            fontSize: '2.2rem', // Increase the font size
            textAlign: 'center', // Center align the text
            letterSpacing: '0.4px', // Add some spacing between letters for a clean look
            color: 'primary.main', // Change text color to the primary theme color
            marginBottom: 6,
          }}
        >
          Volunteer Opportunities
        </Typography>
        <Grid container spacing={4}>
          {volunteerOpportunities.slice(0, visibleOpportunities).map((opportunity) => (
            <Grid item key={opportunity.opportunityID} xs={12} sm={6} md={4} lg={3}>
              <Card
                variant="outlined"
                sx={{
                  height: '100%',
                  borderWidth: '1px',
                  boxShadow: 'none', // Removes card shadow
                  borderRadius: '12px', // Custom border radius (adjust the value)
                  display: 'flex', // Use flexbox to control the layout
                  flexDirection: 'column', // Stack children vertically
                }}
              >
                {/* Image Section */}
                <CardMedia
                  component="img"
                  alt={opportunity.title}
                  height="200"
                  image={
                    opportunity.volunteerImageUrl
                      ? `http://localhost:8080${opportunity.volunteerImageUrl}`  // Use the volunteerImageUrl if available
                      : 'http://localhost:3000/images/default.png'

                      // Fallback to default image
                  }
                  title={opportunity.title}
                />

                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h5" gutterBottom>
                    {opportunity.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" paragraph>
                    {opportunity.description.slice(0, 60)}...
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Date: {new Date(opportunity.volunteerDatetime).toLocaleDateString()}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Location: {opportunity.location}
                  </Typography>
                </CardContent>
                <CardActions sx={{ marginTop: 'auto' }}>
                  <Button
                    size="small"
                    color="primary"
                    sx={{
                      color: 'purple', // Set the text color to purple
                      textTransform: 'none', // Removes uppercase text
                      padding: '6px 16px', // Add padding to the button (vertical, horizontal)
                      marginBottom: '4px', // Reduce bottom margin
                      '&:hover': {
                        color: '#675bc8',
                        borderRadius: '8px',
                      },
                    }}
                    onClick={() => navigate(`/opportunity/${opportunity.opportunityID}`)}
                  >
                    Read More
                  </Button>

                </CardActions>
              </Card>

            </Grid>
          ))}
        </Grid>
        {/* Show More / Show Less Button */}
        {volunteerOpportunities.length > 4 && (
          <Button
            variant="outlined"
            onClick={handleShowMoreLess}
            sx={{
              marginTop: '2rem',
              display: 'block',  // Center button horizontally
              marginLeft: 'auto',
              marginRight: 'auto',
              borderColor: 'purple',  // Set the border color to purple
              borderRadius: '8px',    // Match border radius to the first button's style
              padding: '6px 16px',    // Match padding to the first button's style
              color: 'purple',        // Set the text color to purple
              textTransform: 'none',  // Removes uppercase text
              marginBottom: '4px',    // Add margin-bottom to match the first button's margin
              '&:hover': {
                borderColor: '#9b59b6', // Set a slightly darker purple for the border on hover
                backgroundColor: '#f0e6f9', // Light purple background on hover
                color: '#9b59b6',      // Set text color to darker purple on hover
              },
              '&.MuiButton-root.Mui-focused': {
                borderColor: 'purple',  // Keep purple border color when focused
              },
              '&.MuiButton-root.Mui-active': {
                borderColor: 'purple',  // Keep purple border color when clicked (active state)
                backgroundColor: 'transparent', // Make sure the background stays transparent
              }
            }}
          >
            {isExpanded ? 'Show Less' : 'Show More'}
          </Button>
        )}
      </Container>
      {/* Dialog for the CreateOpportunity form */}
      <Dialog
        open={openFormDialog}
        onClose={handleCloseFormDialog}
        aria-labelledby="form-dialog-title"
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle id="form-dialog-title" sx={{ position: 'relative' }}>
          Schedule Volunteer Work
          <IconButton
            onClick={handleCloseFormDialog}
            sx={{
              position: 'absolute',
              right: 8,
              top: 8,
              color: 'gray',
              '&:hover': {
                backgroundColor: 'transparent',
              }
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <CreateOpportunity onSubmitSuccess={handleFormSubmitSuccess} />
        </DialogContent>
      </Dialog>

    </motion.div>
  );
};

export default Volunteer;
