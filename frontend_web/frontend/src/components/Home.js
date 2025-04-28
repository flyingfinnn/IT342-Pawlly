
import React, { useState, useEffect } from 'react';
import { TextField, Button, Box, Typography, Snackbar, Grid, Card, CardContent, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, ToggleButton } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // Import useNavigate for redirection
import landingImage from '../assets/landing.png'; // Import the image
import mockImage from '../assets/mock.png';

const Home = () => {
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    author: '',
  });
  const [successMessage, setSuccessMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [articles, setArticles] = useState([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [currentId, setCurrentId] = useState(null);

  const navigate = useNavigate(); // Initialize the navigate hook

  useEffect(() => {
    fetchArticles();
  }, []);

  const fetchArticles = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/newsfeed');
      setArticles(response.data);
    } catch (error) {
      console.error("Error fetching articles:", error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing && currentId) {
        await axios.put(`http://localhost:8080/api/newsfeed/${currentId}`, formData);
        setSuccessMessage('Article updated successfully!');
      } else if (!isEditing) {
        await axios.post('http://localhost:8080/api/newsfeed', formData);
        setSuccessMessage('Article posted successfully!');
      } else {
        setSuccessMessage('Update failed. Article ID is missing.');
      }
      setSnackbarOpen(true);
      fetchArticles();
      resetForm();
    } catch (error) {
      setSuccessMessage('Submission failed. Try again.');
      setSnackbarOpen(true);
    }
  };

  const resetForm = () => {
    setFormData({ title: '', content: '', author: '' });
    setIsEditing(false);
    setCurrentId(null);
  };

  const handleEdit = (article) => {
    setFormData(article);
    setIsEditing(true);
    setCurrentId(article.articleID);
  };

  const handleDeleteClick = (articleID) => {
    setDeleteId(articleID);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`http://localhost:8080/api/newsfeed/${deleteId}`);
      setArticles(articles.filter(article => article.articleID !== deleteId));
      setDeleteDialogOpen(false);
    } catch (error) {
      setDeleteDialogOpen(false);
    }
  };

  const handleSnackbarClose = () => {
    setSnackbarOpen(false);
  };

  // Function to format the date to a readable format
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  // Redirect to the AdoptionForm page when 'Adopt Now' button is clicked
  const handleAdoptNowClick = () => {
    navigate('/adopt'); // Use navigate to redirect to the AdoptionForm page
  };

  return (
    <Box sx={{ padding: 3 }}>

      {/* Page Layout */}
      <Grid container spacing={3}>
        {/* Left Side */}
        <Grid item xs={5} container justifyContent="center" alignItems="center">
          <Typography variant="h4" sx={{ mb: 2 }}>Give a New Life to PAWLLY</Typography>
          <Typography variant="body1" sx={{ mb: 3 }}>
            Pet adoption and rehoming are both vital aspects of animal welfare, offering hope and a fresh start to pets in need.
            Open your heart and your home to a shelter pet.
          </Typography>
          <Box sx={{ display: 'flex', gap: 4 }}>
            <ToggleButton value="adopt"
              onClick={handleAdoptNowClick} // Add the handleAdoptNowClick function here
              sx={{
                border: '2px solid',
                borderRadius: '8px',
                padding: '12px 36px',
                borderColor: 'primary.main',
                backgroundColor: 'primary.main',
                color: '#fff',
                '&:hover': {
                  backgroundColor: 'white',
                  color: 'primary.main',
                },
              }}
            >
              Adopt Now
            </ToggleButton>

            <ToggleButton value="adopt"
              onClick={handleSubmit}
              sx={{
                border: '2px solid',
                borderRadius: '8px',
                padding: '12px 36px',
                borderColor: 'secondary.main',
                backgroundColor: 'secondary.main',
                color: '#fff',
                '&:hover': {
                  backgroundColor: 'white',
                  color: 'secondary.main',
                },
              }}
            >
              Rehome Now
            </ToggleButton>
          </Box>

        </Grid>

        {/* Right Side */}
        <Grid item xs={7} container justifyContent="center" alignItems="center">
          <img src={landingImage} alt="Pet Adoption" style={{ width: '70%', height: 'auto' }} />
        </Grid>
      </Grid>

      <img src={mockImage} alt="Mock" style={{ width: '80%', height: 'auto', display: 'block', margin: '0 auto'}} />

      {/* News Article Form */}
      <Box component="form" onSubmit={handleSubmit} sx={styles.container}>
        <Typography variant="h6">{isEditing ? 'Update Article' : 'Post a News Article'}</Typography>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            name="title"
            label="Title"
            variant="outlined"
            sx={styles.textField}
            value={formData.title}
            onChange={handleChange}
            required
          />
          <TextField
            name="content"
            label="Content"
            variant="outlined"
            multiline
            rows={4}
            sx={styles.textField}
            value={formData.content}
            onChange={handleChange}
            required
          />
          <TextField
            name="author"
            label="Author"
            variant="outlined"
            sx={styles.textField}
            value={formData.author}
            onChange={handleChange}
          />
        </Box>
        <Button type="submit" variant="contained" color="primary" sx={styles.button}>
          {isEditing ? 'Update' : 'Post'}
        </Button>
      </Box>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
        message={successMessage}
      />

      {/* News Feed Cards */}
      <Typography variant="h4" sx={{ mb: 3 }}>News Feed</Typography>
      <Grid container spacing={3}>
        {articles.map((article) => (
          <Grid item xs={12} sm={6} md={4} key={article.articleID}>
            <Card sx={{ padding: 2 }}>
              <CardContent>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>{article.title}</Typography>
                <Typography variant="body2" sx={{ mb: 1 }}>{article.content}</Typography>
                <Typography variant="body2" sx={{ fontWeight: 'bold' }} color="textSecondary">By {article.author}</Typography>
                <Typography variant="body2" color="textSecondary">
                  Posted on: {formatDate(article.createdAt)}
                </Typography>
                <Box sx={{ mt: 2 }}>
                  <Button color="primary" onClick={() => handleEdit(article)}>Edit</Button>
                  <Button color="secondary" onClick={() => handleDeleteClick(article.articleID)}>Delete</Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Confirmation Dialog */}
      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
      >
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this article?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} color="primary">Cancel</Button>
          <Button onClick={confirmDelete} color="secondary">Delete</Button>
        </DialogActions>
      </Dialog>

    </Box>
  );
};

const styles = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: 3,
    maxWidth: 600,
    margin: '0 auto',
    backgroundColor: 'white',
    padding: 3,
    borderRadius: 2,
    boxShadow: 3,
  },
  textField: {
    width: '100%',
  },
  button: {
    width: '100%',
  },
};

export default Home;
