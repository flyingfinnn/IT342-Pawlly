import React from 'react';
// ABOUT US IMAGES
import AU_Lou from '../assets/AU_Lou.jpg';
import AU_Rig from '../assets/AU_Rig.jpg';
import AU_Kit from '../assets/AU_Kit.jpg';
// MUI COMPONENTS
import { Box, Typography, Grid } from '@mui/material';

const AboutUs = () => {
  const teamMembers = [
    { name: 'Louie James Carbungco', position: 'Founder', image: AU_Lou },
    { name: 'Rigel L. Baltazar', position: 'Founder', image: AU_Rig },
    { name: 'Keith Ruezyl Tagarao', position: 'Founder', image: AU_Kit },
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
