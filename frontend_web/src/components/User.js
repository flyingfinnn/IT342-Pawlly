import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    Box,
    Avatar,
    Typography,
    Container,
    CircularProgress,
    Button,
    Grid,
    Card,
    CardContent,
    CardMedia,
} from "@mui/material";
import axios from "axios";
import VolunteerDashboard from "../components/Volunteer/VolunteerDashboard";

const User = () => {
    const { id } = useParams(); // Get userId from the URL
    const navigate = useNavigate();
    const [user, setUser] = useState(null);
    const [userPosts, setUserPosts] = useState([]); // State for user's lost and found entries
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUserDetails = async () => {
            try {
                const userResponse = await axios.get(`http://localhost:8080/api/users/${id}`);
                setUser(userResponse.data);

                const postsResponse = await axios.get(
                    `http://localhost:8080/api/lostandfound?creatorid=${id}`
                ); // Assuming API supports filtering by `creatorid`
                setUserPosts(postsResponse.data);
            } catch (error) {
                console.error("Error fetching user details or posts:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchUserDetails();
    }, [id]);

    const handleRedirectToProfile = () => {
        navigate("/profile"); // Redirect to Profile.js for logged-in user
    };

    if (loading) {
        return (
            <Box sx={{ display: "flex", justifyContent: "center", mt: 5 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!user) {
        return <Typography color="error">User not found!</Typography>;
    }

    const loggedInUserId = parseInt(localStorage.getItem("userId"), 10);

    return (
        <Container sx={{ mt: 5 }}>
            {/* User Profile */}
            <Box
                sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    p: 4,
                    borderRadius: 2,
                    boxShadow: 3,
                    bgcolor: "background.paper",
                    mb: 4,
                }}
            >
                <Avatar
                    src={user.profilePicture}
                    alt={user.firstName}
                    sx={{
                        width: 120,
                        height: 120,
                        bgcolor: "grey.300",
                        fontSize: "48px",
                        mb: 2,
                    }}
                >
                    {!user.profilePicture && user.firstName[0]}
                </Avatar>
                <Typography variant="h4" fontWeight="bold">
                    {user.firstName} {user.lastName}
                </Typography>
                <Typography color="textSecondary">{user.email}</Typography>
                <Typography color="textSecondary">{user.address}</Typography>
                <Typography color="textSecondary">{user.phoneNumber}</Typography>

                {loggedInUserId === user.userId && (
                    <Button
                        variant="contained"
                        color="primary"
                        sx={{ mt: 3 }}
                        onClick={handleRedirectToProfile}
                    >
                        Edit Your Profile
                    </Button>
                )}
            </Box>

            {/* User's Lost and Found Entries */}
            <Typography variant="h5" sx={{ mb: 3 }}>
                {user.firstName}'s Lost and Found Entries
            </Typography>
            <Grid container spacing={2}>
                {userPosts.map((post) => (
                    <Grid item xs={12} sm={6} md={4} key={post.reportid}>
                        <Card sx={{ height: "100%" }}>
                            <CardMedia
                                component="img"
                                alt={post.description}
                                height="140"
                                image={
                                    post.imageurl
                                        ? `http://localhost:8080${post.imageurl}`
                                        : "http://localhost:8080/images/default_image.jpg"
                                }
                                title={post.description}
                            />
                            <CardContent>
                                <Typography
                                    variant="subtitle1"
                                    fontWeight="bold"
                                    color="primary"
                                >
                                    {post.reporttype === "lost" ? "Lost" : "Found"} -{" "}
                                    {post.petcategory}
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    {post.description}
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Last Seen: {post.lastseen}
                                </Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Date Reported: {post.datereported}
                                </Typography>
                            </CardContent>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <VolunteerDashboard />
        </Container>
    );
};

export default User;
