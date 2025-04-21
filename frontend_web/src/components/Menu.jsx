import React from "react";
import { AppBar, Toolbar, Button, Box, Typography } from "@mui/material";
import { Link } from "react-router-dom";

const Menu = () => (
  <AppBar position="fixed" color="primary" sx={{ mb: 4, boxShadow: "0px 4px 10px rgba(0, 0, 0, 0.2)" }}>
    <Toolbar sx={{ display: "flex", justifyContent: "space-between", height: 60, padding: "0 20px" }}>
      <Box display="flex" alignItems="center">
        <Typography variant="h5" sx={{ fontWeight: 700, letterSpacing: 1 }}>
          Purr: Adoption System
        </Typography>
      </Box>
      <Box display="flex" alignItems="center">
        <Button color="inherit" component={Link} to="/" sx={{ mx: 1, fontWeight: 600 }}>
          Adopt A Pet
        </Button>
        <Button color="inherit" component={Link} to="/admin" sx={{ mx: 1, fontWeight: 600 }}>
          Admin
        </Button>
      </Box>
    </Toolbar>
  </AppBar>
);

export default Menu;
