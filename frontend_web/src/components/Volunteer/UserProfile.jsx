import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Card, CardContent, Typography, Container, Box, Avatar } from '@mui/material';
import { useParams } from 'react-router-dom';

const UserProfile = () => {
    const [users, setUsers] = useState([]);
    const [opportunity, setOpportunity] = useState(null);
    const { id } = useParams();  // Get the 'id' parameter from the URL

    useEffect(() => {
        fetchUsers();
        fetchOpportunity();
    }, [id]);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/users');
            console.log('Fetched Users:', response.data); // Log the fetched data
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const fetchOpportunity = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/volunteer/opportunity/${id}`);
            console.log('Fetched Opportunity:', response.data);
            setOpportunity(response.data);
        } catch (error) {
            console.error('Error fetching opportunity:', error);
        }
    };

    // If opportunity is fetched, find the user whose userId matches creatorId
    const filteredUser = users.find(user => user.userId === opportunity?.creatorId);

return (
    <Container sx={{ padding: 0 }}>  {/* Remove padding from Container */}
        <Box sx={{ display: 'flex-start', justifyContent: 'center', flexWrap: 'wrap', gap: 3, marginTop: 4 }}>
            {filteredUser && (
                <Card key={filteredUser.userId} sx={{ width: '100%', maxWidth: 500, borderRadius: 2, boxShadow: 0, padding: 0, backgroundColor: '#F4F4FB', marginBottom: 3 }}>
                    <CardContent sx={{ display: 'flex', alignItems: 'center' }}>
                        {/* Created by label */}
                        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginRight: 3 }}>
                            <Avatar alt={filteredUser.firstName} src={`data:image/jpeg;base64,${filteredUser.profilePicture}`} sx={{ width: 80, height: 80, borderRadius: '50%' }} />
                            
                        </Box>

                        {/* User details on the right side */}
                        <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                                {filteredUser.username} {/* Displaying the username here */}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                Firstname: {filteredUser.firstName}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                Lastname: {filteredUser.lastName}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                Phone: {filteredUser.phoneNumber}
                            </Typography>
                            <Typography variant="body2" color="textSecondary">
                                Email: {filteredUser.email}
                            </Typography>
                        </Box>
                    </CardContent>
                </Card>
            )}
        </Box>
    </Container>
);

};

export default UserProfile;
