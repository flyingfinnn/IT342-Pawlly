import React, { useState } from "react";
import axios from "axios";

import "./Login.css";
import {
  Button,
  TextField,
  Container,
  Typography,
  Box,
  Grid,
  Paper,
  IconButton,
  InputAdornment,
} from "@mui/material"; // Import Material UI components
import logo from "../assets/logo_colored.png";
import { Link, useNavigate } from "react-router-dom";
import { Visibility, VisibilityOff } from "@mui/icons-material";

const Login = () => {
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const navigate = useNavigate();

  const [showPassword, setShowPassword] = useState(false);

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        credentials,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      console.log("Response data:", response.data);

      const token = response.data;

      localStorage.setItem("token", token); // Save the token if authentication is successful
      alert("Logged in successfully");
      navigate("/profile");
    } catch (error) {
      alert("Error logging in. Invalid username or password.");
      console.error("Error during login:", error);
    }
  };

  return (
    <>
      <Container maxWidth="md">
        {" "}
        {/* Set the container width to md (medium) for better control */}
        <Grid
          container
          spacing={0}
          alignItems="center"
          justifyContent="center"
          style={{ minHeight: "100vh" }}
        >
          {/* Left side - Logo or Placeholder */}
          <Grid
            item
            xs={12}
            sm={6}
            style={{
              backgroundColor: "#f5f5f5",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <Box textAlign="center">
              <img
                src={logo} // Use the imported logo
                alt="Purr Logo"
                style={{ maxWidth: "100%", height: "auto" }} // Ensure responsiveness
              />
            </Box>
          </Grid>
          {/* Right side - Login Form */}
          <Grid item xs={12} sm={6}>
            <Paper elevation={6} sx={{ p: 3 }}>
              <Box sx={{ mt: 0, mb: 3 }}>
                <Typography variant="h4" align="center">
                  Login
                </Typography>
              </Box>
              <form onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                  <Grid item xs={12}>
                    <TextField
                      label="Username"
                      name="username"
                      variant="outlined"
                      fullWidth
                      value={credentials.username}
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
                      value={credentials.password}
                      onChange={handleChange}
                      required
                      InputProps={{
                        endAdornment: (
                          <InputAdornment position="end">
                            <IconButton onClick={toggleShowPassword} edge="end">
                              {showPassword ? (
                                <VisibilityOff />
                              ) : (
                                <Visibility />
                              )}
                            </IconButton>
                          </InputAdornment>
                        ),
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <Button
                      type="submit"
                      variant="contained"
                      color="primary"
                      fullWidth
                    >
                      Login
                    </Button>
                  </Grid>
                  <Grid item xs={12}>
                    {/* Link to the Signup page */}
                    <Typography variant="body2" align="center">
                      Don't have an account? <Link to="/signup">Sign up.</Link>
                    </Typography>
                  </Grid>
                </Grid>
              </form>
            </Paper>
          </Grid>
        </Grid>
      </Container>
    </>
  );
};

export default Login;
