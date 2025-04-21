import React, { useState, useEffect } from 'react';
import { Button, Box, Typography, Snackbar, Grid, Card, CardContent, ToggleButton } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import landingImage from '../assets/landing.png';
import mockImage from '../assets/mock.png';
import petPlaceholder from '../assets/petplaceholder.png';

//ABOUT US IMAGES
import AU_Lou from '../assets/AU_Lou.jpg';
import AU_Jai from '../assets/AU_Jai.jpg';
import AU_Stela from '../assets/AU_Stela.jpg';
import AU_Nek from '../assets/AU_Nek.jpg';
import AU_Kiyo from '../assets/AU_Kiyo.jpg';
import AU_Selma from '../assets/AU_Selma.jpg';

const Home = () => {
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        author: '',
        link: '', // Add link field
        image: '',
      });
      
  const [articles, setArticles] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    fetchArticles();
  }, []);

  const fetchArticles = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/newsfeed');
      const updatedArticles = response.data.map((article) => ({
        ...article,
        image: article.imageUrl ? `http://localhost:8080${article.imageUrl}` : petPlaceholder,
      }));
      setArticles(updatedArticles);
    } catch (error) {
      console.error('Error fetching articles:', error);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <Box>
      {/* Top Page Layout */}
      <Grid container spacing={3} sx={{ padding: '50px' }}>
        <Grid item xs={5} container justifyContent="center" alignItems="center">
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 10, alignItems: 'center', textAlign: 'center' }}>
            <Typography variant="h3" style={{ fontWeight: 'bold' }}>Give a New Life to PURR</Typography>
            <Typography variant="h5">
              Pet adoption and rehoming are vital aspects of animal welfare, offering hope and a fresh start to pets in need.
              Open your heart and your home to a shelter pet.
            </Typography>
            <Box sx={{ display: 'flex', gap: 4 }}>
              <ToggleButton
                value="adopt"
                onClick={() => navigate('/adopt')}
                sx={{
                  border: '2px solid',
                  borderRadius: '8px',
                  padding: '12px 36px',
                  borderColor: 'primary.main',
                  backgroundColor: 'primary.main',
                  color: '#fff',
                  '&:hover': { backgroundColor: 'white', color: 'primary.main' },
                }}
              >
                Adopt Now
              </ToggleButton>

              <ToggleButton
                value="rehome"
                onClick={() => navigate('/adopt')}
                sx={{
                  border: '2px solid',
                  borderRadius: '8px',
                  padding: '12px 36px',
                  borderColor: 'secondary.main',
                  backgroundColor: 'secondary.main',
                  color: '#fff',
                  '&:hover': { backgroundColor: 'white', color: 'secondary.main' },
                }}
              >
                Rehome Now
              </ToggleButton>
            </Box>
          </Box>

        </Grid>

        <Grid item xs={7} container justifyContent="center" alignItems="center">
          <img src={landingImage} alt="Pet Adoption" style={{ width: '70%', height: 'auto' }} />
        </Grid>
      </Grid>

      <img src={mockImage} alt="Mock" style={{ width: '80%', height: 'auto', display: 'block', margin: '0 auto' }} />


      {/* News Feed Cards */}
      <Box sx={{ display: 'flex', justifyContent: 'center', paddingTop: '50px' }}>
        <Typography variant="h4" sx={{ my: 3, fontWeight: 'bold' }}>
          Pet News
        </Typography>
      </Box>

      <Grid container spacing={3} sx={{ padding: 3 }}>
        {articles.map((article) => (
          <Grid item xs={12} sm={6} md={4} key={article.articleID}>
            <Card sx={{ display: 'flex', flexDirection: 'column', position: 'relative', padding: 2, height: '100%' }}>
              <Box
                sx={{
                  width: '100%',
                  height: 180,
                  backgroundImage: `url(${article.image})`,
                  backgroundSize: 'cover',
                  backgroundPosition: 'center',
                  borderRadius: 1,
                }}
              />
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="h6" style={{ fontWeight: 'bold' }}>{article.title || 'Untitled'}</Typography>
                <Typography variant="body2" sx={{ my: 1 }}>
                  {article.content.length > 100 ? `${article.content.substring(0, 100)}...` : article.content || 'No content available.'}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {`By ${article.author || 'Unknown'} | Published: ${article.publishedDate ? formatDate(article.publishedDate) : 'N/A'}`}
                </Typography>
              </CardContent>

              <Box
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  paddingTop: 1,
                  marginTop: 'auto',
                }}
              >
                {/* Learn More Button */}
                <Button
                    variant="outlined"
                    color="primary"
                    onClick={() => window.open(article.link, '_blank')} // Opens link in a new tab
                    disabled={!article.link} // Disable if no link is provided
                    >
                    Learn More
                    </Button>

              </Box>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Box sx={{ padding: 25 }}>
        <Typography
          variant="h4"
          align="center"
          sx={{ mb: 4, fontWeight: 'bold', textTransform: 'uppercase' }}
        >
          About Us
        </Typography>
        <Grid container spacing={3} justifyContent="center" sx={{ paddingTop: '50px' }}>
          {[
            { name: 'Louie James Carbungco', position: 'Founder', image: AU_Lou },
            { name: 'Jierelle Jane Ravanes', position: 'Founder', image: AU_Jai },
            { name: 'Stela Maris Asufra', position: 'Founder', image: AU_Stela },
            { name: 'Nick Carter Lacanglacang', position: 'Founder', image: AU_Nek },
            { name: 'Yoshinori Kyono Jr.', position: 'Founder', image: AU_Kiyo },
            { name: 'John Edward Selma', position: 'Founder', image: AU_Selma },
          ].map((person, index) => (
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

    </Box>

    
  );
};

export default Home;
