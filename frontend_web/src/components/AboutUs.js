import React from 'react';
// ABOUT US IMAGES
import AU_Lou from '../assets/AU_Lou.jpg';
import AU_Jai from '../assets/AU_Jai.jpg';
import AU_Stela from '../assets/AU_Stela.jpg';
import AU_Nek from '../assets/AU_Nek.jpg';
import AU_Kiyo from '../assets/AU_Kiyo.jpg';
import AU_Selma from '../assets/AU_Selma.jpg';
import { Box, Typography, Grid } from '@mui/material';

const AboutUs = () => {
  const teamMembers = [
    { name: 'Louie James Carbungco', position: 'Founder', image: AU_Lou },
    { name: 'Jierelle Jane Ravanes', position: 'Founder', image: AU_Jai },
    { name: 'Stela Maris Asufra', position: 'Founder', image: AU_Stela },
    { name: 'Nick Carter Lacanglacang', position: 'Founder', image: AU_Nek },
    { name: 'Yoshinori Kyono Jr.', position: 'Founder', image: AU_Kiyo },
    { name: 'John Edward Selma', position: 'Founder', image: AU_Selma },
  ];

  return (
    <Box sx={{ padding: 25 }}>
      <Typography
        variant="h4"
        align="center"
        sx={{ mb: 4, fontWeight: 'bold', textTransform: 'uppercase' }}
      >
        About Us
      </Typography>
      <Grid container spacing={3} justifyContent="center" sx={{ paddingTop: '50px' }}>
        {teamMembers.map((person, index) => (
          <Grid
            item
            xs={12}
            sm={6}
            md={4}
            key={index}
            sx={{ textAlign: 'center' }}
          >
            <Box
              sx={{
                width: 200,
                height: 200,
                borderRadius: '50%',
                backgroundImage: `url(${person.image})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                margin: '0 auto',
              }}
            />
            <Typography variant="h6" sx={{ mt: 2 }}>
              {person.name}
            </Typography>
            <Typography variant="body2">{person.position}</Typography>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default AboutUs;
