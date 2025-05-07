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
    IconButton,
    TextField,
    Typography,
    Container,
    Stack,
} from "@mui/material";
import { Edit, Delete, Save, Close } from "@mui/icons-material";

const UserDashboard = () => {
    const [users, setUsers] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");
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
            const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/users`);
            setUsers(response.data);
        } catch (error) {
            console.error("Error fetching users:", error);
        }
    };

    const handleEdit = (user) => {
        setEditingUserId(user.userId);
        setEditFormData({ ...user });
    };

    const handleCancelEdit = () => setEditingUserId(null);

    const handleEditChange = (e) => {
        setEditFormData({ ...editFormData, [e.target.name]: e.target.value });
    };

    const handleSaveEdit = async (id) => {
        const token = localStorage.getItem("token");
        if (!token) return console.error("Token is missing. Please log in again.");

        const formData = new FormData();
        formData.append("user", JSON.stringify(editFormData));

        try {
            const response = await axios.put(
                `${process.env.REACT_APP_BACKEND_URL}/api/users/${id}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            if (response.status === 200) {
                fetchUsers();
                setEditingUserId(null);
            } else {
                console.error("Failed to update user.");
            }
        } catch (error) {
            console.error("Error saving user:", error.response?.data || error.message);
        }
    };

    const handleDelete = async (id) => {
        const token = localStorage.getItem("token");
        if (!token) return console.error("Token is missing. Please log in again.");

        try {
            const response = await axios.delete(
                `${process.env.REACT_APP_BACKEND_URL}/api/users/${id}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (response.status === 200) fetchUsers();
            else console.error("Failed to delete user");
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };

    const filteredUsers = users.filter((user) =>
        `${user.firstName} ${user.lastName} ${user.username}`
            .toLowerCase()
            .includes(searchQuery.toLowerCase())
    );

    return (
        <Container sx={{ mt: 4 }}>
            <Stack direction="row" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h5" fontWeight="bold">User Dashboard</Typography>
                <TextField
                    label="Search"
                    variant="outlined"
                    size="small"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
            </Stack>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: "bold" }}>First Name</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Last Name</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Username</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Email</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Address</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Phone</TableCell>
                            <TableCell sx={{ fontWeight: "bold" }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredUsers.map((user) => (
                            <TableRow key={user.userId}>
                                {["firstName", "lastName", "username", "email", "address", "phoneNumber"].map((field) => (
                                    <TableCell key={field}>
                                        {editingUserId === user.userId ? (
                                            <TextField
                                                name={field}
                                                value={editFormData[field]}
                                                onChange={handleEditChange}
                                                fullWidth
                                                size="small"
                                            />
                                        ) : (
                                            <Typography variant="body2">{user[field]}</Typography>
                                        )}
                                    </TableCell>
                                ))}

                                <TableCell>
                                    {editingUserId === user.userId ? (
                                        <>
                                            <IconButton
                                                color="primary"
                                                onClick={() => handleSaveEdit(user.userId)}
                                            >
                                                <Save />
                                            </IconButton>
                                            <IconButton
                                                color="secondary"
                                                onClick={handleCancelEdit}
                                            >
                                                <Close />
                                            </IconButton>
                                        </>
                                    ) : (
                                        <>
                                            <IconButton
                                                color="primary"
                                                onClick={() => handleEdit(user)}
                                            >
                                                <Edit />
                                            </IconButton>
                                            <IconButton
                                                color="error"
                                                onClick={() => handleDelete(user.userId)}
                                            >
                                                <Delete />
                                            </IconButton>
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
