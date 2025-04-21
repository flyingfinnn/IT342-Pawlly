import React, { useState,useRef, useEffect } from "react";
import { TextField, Button, Typography, Snackbar, Box, Select, MenuItem, InputLabel, FormControl } from "@mui/material";
import axios from "axios";
import { useUser } from '../UserContext';

const RehomeForm = () => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    name: "",
    petType: "",
    breed: "",
    description: "",
    image: null,  
    userName: user ? user.firstName + ' ' + user.lastName : '',
    address: user ? user.address : '',
    contactNumber: user ? user.phoneNumber : '',
    submissionDate: "",
    age: "",
    gender: "",  
  });
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [ageError, setAgeError] = useState(""); 
  const [contactError, setContactError] = useState(""); 

  const fileInputRef = useRef(null); 

  useEffect(() => {
    if (user) {
      setFormData((prevData) => ({
        ...prevData,
        userName: user.firstName + ' ' + user.lastName,
        address: user.address,
        contactNumber: user.phoneNumber,
      }));
    }
  }, [user]);

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    setFormData({
        ...formData,
        image: file,
    });
};

  
  const handleChange = (e) => {
    const { name, value, files } = e.target;

    
    if (name === "contactNumber") {
        if (!/^\d*$/.test(value)) {
            setContactError("Contact number must be numeric");
            return;
        } else {
            setContactError(""); // Clear error if valid
        }
    }

    
    if (name === "age") {
        if (!/^\d*$/.test(value) && (value < 0)) { 
            setAgeError("Age should be a non-negative number"); 
            return;
            setAgeError(""); // Clear error if valid
        }
    }

    
    setFormData((prev) => ({
        ...prev,
        [name]: name === "image" ? files[0] : value,
    }));
  };

  
  const handleSubmit = async (e) => {
    e.preventDefault();
  
    const form = new FormData();
    form.append("name", formData.name);
    form.append("type", formData.petType);
    form.append("breed", formData.breed);
    form.append("age", formData.age);
    form.append("gender", formData.gender);
    form.append("description", formData.description);
    form.append("photo", formData.image);
    form.append("userName", formData.userName);
    form.append("address", formData.address);
    form.append("contactNumber", formData.contactNumber);
    form.append("submissionDate", new Date().toISOString().split('T')[0]);
    form.append("status", "PENDING_REHOME");
  
    try {
      await axios.post("http://localhost:8080/api/pet/postpetrecord", form, {
        headers: { "Content-Type": "multipart/form-data" },
      });
  
      // Set success message
      setSuccessMessage("Pet successfully rehomed!");
      setErrorMessage("");
  
      // Reset the form
      resetForm();
  
      // Reload after delay
      setTimeout(() => {
        window.location.reload();
      }, 1000); // Reload after 3 seconds
    } catch (error) {
      setSuccessMessage("");
      setErrorMessage("There was an error rehoming the pet. Please try again.");
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      petType: '',
      breed: '',
      age: '',
      gender: '', 
      description: '',
      image: null,
      userName: '',
      address: '',
      contactNumber: '',
      submissionDate: ''
    });
    if (fileInputRef.current) {
      fileInputRef.current.value = ""; 
    }
  };
  

  const styles = {
    container: {
      padding: "8px", 
      maxWidth: "1200px", 
      margin: "0 auto", 
    },
    columns: {
      display: "flex",
      justifyContent: "space-between",
      flexWrap: "wrap", 
      gap: "10px",
    },
    leftColumn: {
      flex: 1,
      border: "2px solid #5A20A8",
      padding: "10px", 
      boxSizing: "border-box",
    },
    rightColumn: {
      flex: 1,
      padding: "10px", 
      boxSizing: "border-box",
    },
    buttonContainer: {
      marginTop: "10px", 
      display: "flex",
      justifyContent: "center",
    },
  };

  return (
    <div style={styles.container}>
      <Typography variant="h4" sx={{ color: "#5A20A8", marginBottom: 2, fontFamily: "'Caramel', sans-serif", fontWeight: "bold", textAlign: "center" }}>
        Rehome Your Pet
      </Typography>
      <div style={styles.columns}>
        <div style={styles.leftColumn}>
          <TextField
            label="Name of Pet"
            name="name"
            fullWidth
            value={formData.name}
            onChange={handleChange}
            required
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Pet Type"
            name="petType"
            fullWidth
            value={formData.petType}
            onChange={handleChange}
            required
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Breed"
            name="breed"
            fullWidth
            value={formData.breed}
            onChange={handleChange}
            required
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Age"
            name="age"
            fullWidth
            value={formData.age}
            onChange={handleChange}
            required
            type="number"
            error={!!ageError} // Show error state
            helperText={ageError} // Display error message
            sx={{ marginBottom: 2 }}
          />

          {/* Gender dropdown */}
          <FormControl fullWidth sx={{ marginBottom: 2 }}>
            <InputLabel>Gender</InputLabel>
            <Select
              name="gender"
              value={formData.gender}
              onChange={handleChange}
              required
            >
              <MenuItem value="Male">Male</MenuItem>
              <MenuItem value="Female">Female</MenuItem>
            </Select>
          </FormControl>

          <TextField
            label="Description"
            name="description"
            fullWidth
            value={formData.description}
            onChange={handleChange}
            required
            multiline
            rows={4}
            sx={{ marginBottom: 2 }}
          />
          {/* Image upload field */}
          <input
            type="file"
            name="image"
            onChange={handleChange}
            accept="image/*"
            required
            ref={fileInputRef}
            style={{ marginBottom: "16px", display: "block" }}
          />
        </div>

        {/* Adopter's Info */}
        <div style={styles.rightColumn}>
          <TextField
            label="Your Name"
            name="userName"
            fullWidth
            value={formData.userName}
            onChange={(e) => setFormData({ ...formData, userName: e.target.value })}
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Address"
            name="address"
            fullWidth
            value={formData.address}
            onChange={(e) => setFormData({ ...formData, address: e.target.value })}
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Contact Number"
            name="contactNumber"
            fullWidth
            value={formData.contactNumber}
            onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
            sx={{ marginBottom: 2 }}
          />
          <TextField
            label="Submission Date"
            type="date"
            name="submissionDate"
            value={formData.submissionDate || new Date().toISOString().split('T')[0]} 
            inputProps={{
              min: new Date().toISOString().split('T')[0], 
              max: new Date().toISOString().split('T')[0]  
            }}
            variant="outlined"
            required
            fullWidth
            sx={{ marginBottom: 2 }}
          />

        </div>
      </div>
      <div style={styles.buttonContainer}>
        <Button
          variant="contained"
          sx={{
            backgroundColor: "#5A20A8",
            color: "white",
            "&:hover": { backgroundColor: "#431880" },
            width: "200px",
          }}
          onClick={handleSubmit}
        >
          Confirm Rehome
        </Button>
      </div>

      {/* Success Snackbar */}
      <Snackbar
        open={!!successMessage}
        autoHideDuration={3000}
        onClose={() => setSuccessMessage("")}
        message={successMessage}
        sx={{
          '& .MuiSnackbarContent-root': {
            backgroundColor: '#5A20A8',
            color: 'white'
          }
        }}
      />

      {/* Error Snackbar */}
      <Snackbar
        open={!!errorMessage}
        autoHideDuration={4000}
        onClose={() => setErrorMessage("")}
        message={errorMessage}
        sx={{
          '& .MuiSnackbarContent-root': {
            backgroundColor: '#5A20A8',
            color: 'white'
          }
        }}
      />
    </div>
  );
};

export default RehomeForm;