import React, { useState } from 'react';
import { Box, CssBaseline, Typography } from '@mui/material';
import UserDashboard from './UserDashboard';
import AdoptionDashboard from './AdoptionDashboard';
import PetDashboard from './PetDashboard';
import VolunteerDashboard from './VolunteerDashboard';
import AdminNavbar from './AdminNavbar';
import ArticleDashboard from './ArticleDashboard';

const AdminDashboard = () => {
    const [selectedTab, setSelectedTab] = useState('Users');

    const renderContent = () => {
        switch (selectedTab) {
            case 'Users':
                return <UserDashboard />;
            case 'Adoptions':
                return <AdoptionDashboard />;
            case 'Rehome':
                return <PetDashboard />;
            case 'Articles':
                return <ArticleDashboard />;
            case 'Rehome':
                return <PetDashboard />;
            case 'Volunteers':
                return <VolunteerDashboard />;
            default:
                return <UserDashboard />;
        }
    };

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                minHeight: '100vh',
                overflow: 'hidden', // Prevent black spots caused by overflow
            }}
        >
            <CssBaseline />
            <Box
                sx={{
                    display: 'flex',
                    flex: 1,
                    pt: 2,
                    overflow: 'auto', // Enable scrolling if content exceeds viewport
                }}
            >
                <AdminNavbar selectedTab={selectedTab} setSelectedTab={setSelectedTab} />
                <Box
                    component="main"
                    sx={{
                        flexGrow: 1,
                        padding: 3,
                        backgroundColor: '#FFFFFF', // Set a consistent background color
                        minHeight: '100%', // Ensure main area covers the viewport height
                    }}
                >
                    <Typography variant="h4" align="center" sx={{ marginBottom: 2 }}>
                        {selectedTab} Dashboard
                    </Typography>
                    {renderContent()}
                </Box>
            </Box>
        </Box>
    );
};

export default AdminDashboard;
