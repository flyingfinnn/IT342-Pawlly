import React from 'react';
import { Drawer, List, ListItem, ListItemIcon, ListItemText } from '@mui/material';
import { People, Favorite, Pets, Event, Settings, Logout, AttachMoney, Article } from '@mui/icons-material';

const sections = [
    { label: 'Users', icon: <People /> },
    { label: 'Adoptions', icon: <Favorite /> },
    { label: 'Rehome', icon: <Favorite /> },
    { label: 'Donation', icon: <AttachMoney /> },
    { label: 'Sponsorships', icon: <Pets /> },
    { label: 'Lost and Found', icon: <Event /> },
    { label: 'Volunteers', icon: <People /> },
    { label: 'Events', icon: <Event /> },
    { label: 'Articles', icon: <Article /> },
    { label: 'Settings', icon: <Settings /> },
    { label: 'Logout', icon: <Logout /> },
];

const AdminNavbar = ({ selectedTab, setSelectedTab }) => (
    <Drawer
        variant="permanent"
        sx={{
            width: 240,
            flexShrink: 0,
            position: 'relative', // Ensure it is relative to the layout
            [`& .MuiDrawer-paper`]: {
                width: 240,
                boxSizing: 'border-box',
                position: 'relative', // Stops floating above content
            },
        }}
    >
        <List>
            {sections.map((section) => (
                <ListItem
                    button
                    key={section.label}
                    selected={selectedTab === section.label}
                    onClick={() => setSelectedTab(section.label)}
                >
                    <ListItemIcon>{section.icon}</ListItemIcon>
                    <ListItemText primary={section.label} />
                </ListItem>
            ))}
        </List>
    </Drawer>
);

export default AdminNavbar;
