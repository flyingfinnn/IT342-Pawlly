import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

// Example image URL, replace with your own image or use an asset.
import ErrorImage from '../assets/errorimage.png';

const NoMatch = () => {
  const navigate = useNavigate();

  return (
    <Box
      display="flex"
      flexDirection="column"
      justifyContent="center"
      alignItems="center"
      height="100vh"
      textAlign="center"
      padding={2}
    >
      <Typography variant="h4" gutterBottom>
        Something is wrong
      </Typography>
      
      <Box mb={3}>
        <img
          src={ErrorImage} // Replace with the path to your image
          alt="Error"
          style={{
            maxWidth: '550px',  // You can adjust this value for the desired image size
            width: '100%',      // Ensure responsiveness
            height: 'auto',     // Maintain aspect ratio
            borderRadius: '8px'
          }}
        />
      </Box>

      <Typography variant="body1" marginBottom={3}>
        The page you are looking for was not found.
      </Typography>

      <Button
        variant="contained"
        color="primary"
        onClick={() => navigate('/')}
      >
        Go to Home
      </Button>
    </Box>
  );
};

export default NoMatch;
