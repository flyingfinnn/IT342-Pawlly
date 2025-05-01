import React, { useState, useEffect } from "react";
import {
  Container,
  Grid,
  Typography,
  TextField,
  Button,
  Box,
  IconButton,
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import CreatePostDialog from "../LostAndFound/CreatePostDialog";
import PostCard from "../LostAndFound/PostCard";
import axios from "axios";

const LostAndFoundDashboard = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [lostItems, setLostItems] = useState([]);
  const [postToEdit, setPostToEdit] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  const fetchLostItems = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/lostandfound");
      setLostItems(response.data);
    } catch (error) {
      console.error("Error fetching lost items:", error);
    }
  };

  useEffect(() => {
    fetchLostItems();
  }, []);

  const handleTextFieldClick = () => {
    setOpenDialog(true);
    setIsEditing(false);
    setPostToEdit(null);
  };

  const handleEditPost = (post) => {
    console.log("Editing post:", post);
    setPostToEdit(post);
    setIsEditing(true);
    setOpenDialog(true);
  };

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };

  const handleDeletePost = async (reportId) => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("You are not authenticated. Please log in first.");
      return;
    }

    try {
      await axios.delete(`http://localhost:8080/api/lostandfound/${reportId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      alert("Post deleted successfully");
      fetchLostItems();
    } catch (error) {
      console.error("Error deleting post:", error);
    }
  };

  const filteredItems = lostItems.filter(
    (item) =>
      item.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.reporttype.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.petcategory.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <>
      {/* Admin Dashboard Header */}
      <Container sx={{ mt: 5 }}>
        {/* Create Post Dialog */}
        <CreatePostDialog
          open={openDialog}
          setOpen={setOpenDialog}
          fetchLostItems={fetchLostItems}
          postToEdit={postToEdit}
          isEditing={isEditing}
          setIsEditing={setIsEditing}
          setPostToEdit={setPostToEdit}
        />

        {/* Posts Grid */}
        <Grid container spacing={2}>
          {filteredItems.map((item) => (
            <Grid item key={item.reportid} xs={12} sm={6} md={4}>
              <PostCard
                item={item}
                fetchLostItems={fetchLostItems}
                onEdit={handleEditPost}
                onDelete={() => handleDeletePost(item.reportid)}
              />
            </Grid>
          ))}
        </Grid>
      </Container>
    </>
  );
};

export default LostAndFoundDashboard;
