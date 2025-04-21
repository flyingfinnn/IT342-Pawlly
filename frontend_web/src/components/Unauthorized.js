import React from "react";
import { Box, Typography } from "@mui/material";

const Unauthorized = () => {
    return (
        <Box
            sx={{
                display: "flex",
                justifyContent: "center",  // Center horizontally
                alignItems: "center",      // Center vertically
                height: "100%" , // Full screen minus the AppBar height (64px for Material UI AppBar default height)
                textAlign: "center",       // Center text
                paddingTop: "15em",
                overflow: "hidden"
                // Adjust top padding to avoid overlap with AppBar
            }}
        >
            <div>
                <Typography variant="h1" gutterBottom sx={{ fontWeight: "bold", fontFamily: "'Caramel', sans-serif", color: "#cc0000" }}>
                    Unauthorized
                </Typography>
                <Typography variant="h6">
                    You do not have permission to access this page.
                </Typography>
            </div>
        </Box>
    );
};

export default Unauthorized;
