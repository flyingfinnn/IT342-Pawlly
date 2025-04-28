import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, TextField, Typography, Container, Box } from '@mui/material';

const UserDashboard = () => {
    const [users, setUsers] = useState([]);
    const [editingUserId, setEditingUserId] = useState(null);
    const [editFormData, setEditFormData] = useState({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        address: '',
        phoneNumber: ''
    });

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/users');
            console.log('Fetched Users:', response.data); // Log the fetched data
            setUsers(response.data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const handleEdit = (user) => {
        setEditingUserId(user.userId);
        setEditFormData({
            firstName: user.firstName,
            lastName: user.lastName,
            username: user.username,
            email: user.email,
            address: user.address,
            phoneNumber: user.phoneNumber
        });
    };

    const handleCancelEdit = () => {
        setEditingUserId(null); // Exit edit mode
    };

    const handleEditChange = (e) => {
        setEditFormData({
            ...editFormData,
            [e.target.name]: e.target.value
        });
    };

    const handleSaveEdit = async (id) => {
        const token = localStorage.getItem('token');

        if (!token) {
            console.error("Token is missing. Please log in again.");
            return;
        }

        const formData = new FormData();
        formData.append("user", JSON.stringify(editFormData));
        // if (profilePicture) { // Assume you have a state for the profile picture
        //     formData.append("profilePicture", profilePicture);
        // }

        try {
            const response = await axios.put(
                `http://localhost:8080/api/users/${id}`,
                formData,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data', // Change to multipart/form-data
                    }
                }
            );

            if (response.status === 200) {
                console.log("User updated successfully");
                fetchUsers(); // Refresh the user list after saving
                setEditingUserId(null); // Exit edit mode after saving
            } else {
                console.error("Failed to update user");
            }
        } catch (error) {
            console.error("Error saving user:", error);
        }
    };



    const handleDelete = async (id) => {

        const token = localStorage.getItem('token');  // or wherever you store the token

        if (!token) {
            console.error("Token is missing. Please log in again.");
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/users/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,  // Include the token here
                    'Content-Type': 'application/json',
                },
            });

            if (response.status === 403) {
                console.error("Access denied: You don't have permission to delete this user.");
            } else if (response.ok) {
                alert("User deleted successfully");
                console.log("User deleted successfully");
                // Handle success (e.g., update the UI)
                window.location.reload();
            } else {
                console.error("Failed to delete user");
            }
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };



    return (
        <>
            <Container>

                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>First Name</TableCell>
                                <TableCell>Last Name</TableCell>
                                <TableCell>Username</TableCell>
                                <TableCell>Email</TableCell>
                                <TableCell>Address</TableCell>
                                <TableCell>Phone Number</TableCell>
                                <TableCell>Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.map((user, index) => (
                                <TableRow key={user.userId || index}>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="firstName"
                                                value={editFormData.firstName}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.firstName
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="lastName"
                                                value={editFormData.lastName}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.lastName
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="username"
                                                value={editFormData.username}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.username
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="email"
                                                value={editFormData.email}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.email
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="address"
                                                value={editFormData.address}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.address
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name="phoneNumber"
                                                value={editFormData.phoneNumber}
                                                onChange={handleEditChange}
                                                fullWidth
                                            />
                                        ) : (
                                            user.phoneNumber
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {editingUserId === user.userId ? (
                                            <>
                                                <Button variant="contained" color="primary" onClick={() => handleSaveEdit(user.userId)}>
                                                    Save
                                                </Button>
                                                <Button variant="contained" color="secondary" onClick={handleCancelEdit} sx={{ ml: 2 }}>
                                                    Cancel
                                                </Button>
                                            </>
                                        ) : (
                                            <>
                                                <Button variant="contained" color="primary" onClick={() => handleEdit(user)}>
                                                    Edit
                                                </Button>
                                                <Button variant="contained" color="secondary" onClick={() => handleDelete(user.userId)} sx={{ ml: 2 }}>
                                                    Delete
                                                </Button>
                                            </>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Container>
        </>
    );
};

export default UserDashboard;
