import React, { useState } from "react";
import { AppBar, Toolbar, Box, Typography, Menu, MenuItem } from "@mui/material";
import { NavLink } from "react-router-dom";
import logoWhite from "../images/logo_white.jpg";

const AppMenu = () => {
  const [anchorEl, setAnchorEl] = useState(null);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const linkStyle = ({ isActive }) => ({
    fontWeight: isActive ? "bold" : "normal",
    textDecoration: isActive ? "underline" : "none",
    color: "inherit",
    margin: "0 15px",
    padding: "5px 0",
    transition: "color 0.3s ease",
    "&:hover": {
      color: "#f50057",
    },
  });

  return (
    <AppBar position="fixed" sx={{ mb: 4, backgroundColor: "#333" }}>
      <Toolbar sx={{ display: "flex", justifyContent: "space-between", height: 60 }}>
        <Box display="flex" alignItems="center">
          <NavLink to="/home"> {/* Wrap the image with NavLink */}
            <Box component="img" src={logoWhite} alt="Purr" sx={{ height: 40, mr: 2 }} />
          </NavLink>
          <Typography variant="h6" sx={{ color: "white" }}>
            Purr
          </Typography>
        </Box>
        <Box display="flex" alignItems="center">
          <NavLink to="/home" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>HOME</Typography>
          </NavLink>
          <NavLink to="/lost-and-found" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>Lost and Found</Typography>
          </NavLink>
          <NavLink to="/sponsor" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>Sponsor</Typography>
          </NavLink>
          <NavLink to="/adoption" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>Adoption</Typography>
          </NavLink>
          <NavLink to="/volunteer" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>Volunteer</Typography>
          </NavLink>
          <div onMouseEnter={handleMenuOpen} onMouseLeave={handleMenuClose}>
            <Typography 
              aria-controls={anchorEl ? "admin-menu" : undefined}
              aria-haspopup="true"
              sx={{ cursor: "pointer", color: "white", margin: "0 15px" }}
            >
              Admin
            </Typography>
            <Menu
              id="admin-menu"
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
            >
              <MenuItem onClick={handleMenuClose}>
                <NavLink to="/admin/manage-opportunities" style={{ textDecoration: 'none', color: 'inherit' }}>
                  Manage Opportunities
                </NavLink>
              </MenuItem>
              <MenuItem onClick={handleMenuClose}>
                <NavLink to="/admin/manage-volunteers" style={{ textDecoration: 'none', color: 'inherit' }}>
                  Manage Volunteers
                </NavLink>
              </MenuItem>
            </Menu>
          </div>
          <NavLink to="/login" style={linkStyle}>
            <Typography sx={{ "&:hover": { color: "#f50057" } }}>Login</Typography>
          </NavLink>
          <NavLink to="/signup">
            <Typography
              variant="button"
              sx={{
                color: "secondary.main",
                padding: "5px 10px",
                border: "1px solid transparent",
                borderRadius: "4px",
                "&:hover": {
                  borderColor: "secondary.main",
                  backgroundColor: "rgba(255, 255, 255, 0.1)",
                },
              }}
            >
              Sign Up
            </Typography>
          </NavLink>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default AppMenu; // Changed from Menu to AppMenu
