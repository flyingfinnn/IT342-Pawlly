import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import {
  Button,
  TextField,
  Grid,
  Container,
  Typography,
  Box,
  IconButton,
  InputAdornment,
} from "@mui/material";
import { Visibility, VisibilityOff } from "@mui/icons-material";
import Navbar from "./Navbar";

const Signup = () => {
  const [user, setUser] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
    password: "",
    address: "",
    phoneNumber: "",
  });

  const [showPassword, setShowPassword] = useState(false);

  const navigate = useNavigate();

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/signup",
        user
      ); // Correct API URL
      console.log(response.data);
      alert("Signup successful!");
      navigate("/login");
    } catch (error) {
      console.error("Error during signup:", error);
      alert("Error signing up");
    }
  };

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <>
      <Container maxWidth="xs">
        <Box sx={{ mt: 5, mb: 3 }}>
          <Typography variant="h4" align="center">
            Signup
          </Typography>
        </Box>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            {/* First Name and Last Name side by side */}
            <Grid item xs={12} sm={6}>
              <TextField
                label="First Name"
                name="firstName"
                variant="outlined"
                fullWidth
                value={user.firstName}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                label="Last Name"
                name="lastName"
                variant="outlined"
                fullWidth
                value={user.lastName}
                onChange={handleChange}
                required
              />
            </Grid>
            {/* Stacked fields */}
            <Grid item xs={12}>
              <TextField
                label="Username"
                name="username"
                variant="outlined"
                fullWidth
                value={user.username}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Email"
                name="email"
                type="email"
                variant="outlined"
                fullWidth
                value={user.email}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Password"
                name="password"
                type={showPassword ? "text" : "password"}
                variant="outlined"
                fullWidth
                value={user.password}
                onChange={handleChange}
                required
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={toggleShowPassword} edge="end">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Address"
                name="address"
                variant="outlined"
                fullWidth
                value={user.address}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Phone Number"
                name="phoneNumber"
                variant="outlined"
                fullWidth
                value={user.phoneNumber}
                onChange={handleChange}
                required
              />
            </Grid>
            {/* Signup Button */}
            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                fullWidth
              >
                Signup
              </Button>
            </Grid>
            <Grid item xs={12}>
              {/* Link to the Signup page */}
              <Typography variant="body2" align="center">
                Already have an account? <Link to="/login">Login.</Link>
              </Typography>
            </Grid>
          </Grid>
        </form>
      </Container>
    </>
  );
};

export default Signup;
