import React, { useEffect, useState } from "react";
import axios from "axios";
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Button,
    TextField,
    Typography,
    Container,
} from "@mui/material";

const UserDashboard = () => {
    const [users, setUsers] = useState([]);
    const [editingUserId, setEditingUserId] = useState(null);
    const [editFormData, setEditFormData] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",
        address: "",
        phoneNumber: "",
    });

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/users");
            setUsers(response.data);
        } catch (error) {
            console.error("Error fetching users:", error);
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
            phoneNumber: user.phoneNumber,
        });
    };

    const handleCancelEdit = () => {
        setEditingUserId(null);
    };

    const handleEditChange = (e) => {
        setEditFormData({
            ...editFormData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSaveEdit = async (id) => {
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("Token is missing. Please log in again.");
            return;
        }

        // Prepare FormData
        const formData = new FormData();
        formData.append("user", JSON.stringify(editFormData)); // Convert user object to JSON string

        try {
            const response = await axios.put(
                `http://localhost:8080/api/users/${id}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data", // Explicitly set Content-Type
                    },
                }
            );

            if (response.status === 200) {
                console.log("User updated successfully:", response.data);
                fetchUsers(); // Refresh the list of users
                setEditingUserId(null); // Exit edit mode
            } else {
                console.error("Failed to update user.");
            }
        } catch (error) {
            console.error("Error saving user:", error.response ? error.response.data : error.message);
        }
    };



    const handleDelete = async (id) => {
        const token = localStorage.getItem("token");

        if (!token) {
            console.error("Token is missing. Please log in again.");
            return;
        }

        try {
            const response = await axios.delete(
                `http://localhost:8080/api/users/${id}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (response.status === 200) {
                fetchUsers();
            } else {
                console.error("Failed to delete user");
            }
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };

    return (
        <Container>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 'bold' }}>First Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Last Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Username</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Email</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Address</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Phone Number</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map((user) => (
                            <TableRow key={user.userId}>
                                <TableCell style={{ width: "150px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="firstName"
                                            value={editFormData.firstName}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.firstName}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "150px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="lastName"
                                            value={editFormData.lastName}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.lastName}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "150px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="username"
                                            value={editFormData.username}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.username}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "200px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="email"
                                            value={editFormData.email}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.email}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "250px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="address"
                                            value={editFormData.address}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.address}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "150px" }}>
                                    {editingUserId === user.userId ? (
                                        <TextField
                                            name="phoneNumber"
                                            value={editFormData.phoneNumber}
                                            onChange={handleEditChange}
                                            fullWidth
                                            size="small"
                                            inputProps={{
                                                style: {
                                                    fontSize: "14px",
                                                    padding: "6px",
                                                },
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2">
                                            {user.phoneNumber}
                                        </Typography>
                                    )}
                                </TableCell>
                                <TableCell style={{ width: "250px" }}>
                                    {editingUserId === user.userId ? (
                                        <>
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                onClick={() =>
                                                    handleSaveEdit(user.userId)
                                                }
                                                sx={{ mr: 1 }}
                                            >
                                                Save
                                            </Button>
                                            <Button
                                                variant="outlined"
                                                color="secondary"
                                                onClick={handleCancelEdit}
                                            >
                                                Cancel
                                            </Button>
                                        </>
                                    ) : (
                                        <>
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                onClick={() => handleEdit(user)}
                                                sx={{ mr: 1 }}
                                            >
                                                Edit
                                            </Button>
                                            <Button
                                                variant="contained"
                                                color="secondary"
                                                onClick={() =>
                                                    handleDelete(user.userId)
                                                }
                                            >
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
    );
};

export default UserDashboard;
