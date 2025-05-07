import React from 'react';
import { Drawer, List, ListItem, ListItemIcon, ListItemText } from '@mui/material';
import { People, Favorite, Pets, Event, Settings, Logout, AttachMoney, Article } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../UserContext'; // Import your UserContext hook

const sections = [
    { label: 'Users', icon: <People /> },
    { label: 'Adoptions', icon: <Favorite /> },
    { label: 'Rehome', icon: <Favorite /> },
    //{ label: 'Sponsorships', icon: <Pets /> },
    { label: 'Volunteers', icon: <People /> },
    //{ label: 'Events', icon: <Event /> },
    //{ label: 'Articles', icon: <Article /> },
    //{ label: 'Settings', icon: <Settings /> },
    { label: 'Logout', icon: <Logout />, action: 'logout' },
];

const AdminNavbar = ({ selectedTab, setSelectedTab }) => {
    const navigate = useNavigate();
    const { clearUser } = useUser(); // Use the clearUser function from your context

    const logout = () => {
        clearUser();
        navigate("/home");
    };

    const handleItemClick = (section) => {
        setSelectedTab(section.label);
        if (section.action === 'logout') {
            logout();
        } else {
            // navigate(`/${section.action}`); // Uncomment when needed
        }
    };

    return (
        <Drawer
            variant="permanent"
            sx={{
                width: 240,
                flexShrink: 0,
                position: 'relative',
                top: 64,
                [`& .MuiDrawer-paper`]: {
                    paddingTop: 10,
                    width: 240,
                    boxSizing: 'border-box',
                    position: 'fixed',
                    top: 64,
                },
            }}
        >
            <List>
                {sections.map((section) => (
                    <ListItem
                        button
                        key={section.label}
                        selected={selectedTab === section.label}
                        onClick={() => handleItemClick(section)} // Use handler here
                    >
                        <ListItemIcon>{section.icon}</ListItemIcon>
                        <ListItemText primary={section.label} />
                    </ListItem>
                ))}
            </List>
        </Drawer>
    );
};


export default AdminNavbar;
