import React from "react";
import { AppBar, Toolbar, Button, Box, Typography } from "@mui/material";
import { NavLink } from "react-router-dom";
import logoWhite from "../assets/logo_white.png";

const Menu = () => (
  <AppBar position="fixed" sx={{ mb: 4 }}>
    <Toolbar
      sx={{
        display: "flex",
        justifyContent: "space-between",
        height: 50,
      }}
    >
      <Box display="flex" alignItems="center">
        <Box
          component="img"
          src={logoWhite}
          alt="Purr"
          sx={{ height: 40, mr: 2 }}
        />
        <Typography variant="h6">Purr</Typography>
      </Box>
      <Box display="flex">
        <Button
          color="inherit"
          component={NavLink}
          to="/home"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          HOME
        </Button>
        <Button
          color="inherit"
          component={NavLink}
          to="/lost-and-found"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          Lost and Found
        </Button>
        <Button
          color="inherit"
          component={NavLink}
          to="/sponsor"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          Sponsor
        </Button>
        <Button
          color="inherit"
          component={NavLink}
          to="/adoption"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          Adoption
        </Button>
        <Button
          color="inherit"
          component={NavLink}
          to="/volunteer"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          Volunteer
        </Button>
        <Button
          color="inherit"
          component={NavLink}
          to="/login"
          style={({ isActive }) => ({
            fontWeight: isActive ? "bold" : "normal",
            textDecoration: isActive ? "underline" : "none",
          })}
        >
          Login
        </Button>
        <Button variant="contained" color="secondary">
          Sign Up
        </Button>
      </Box>
    </Toolbar>
  </AppBar>
);

export default Menu;
