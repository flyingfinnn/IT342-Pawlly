import React, { useState, useEffect } from "react";
import {
  Box,
  Card,
  CardContent,
  CardMedia,
  Typography,
  IconButton,
  Stack,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Button,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import axios from "axios";
import { Link } from "react-router-dom";

const PostCard = ({ item, fetchLostItems, onEdit }) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [userId, setUserId] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false); // Admin role check
  const [creatorUsername, setCreatorUsername] = useState("Unknown");
  const [openDialog, setOpenDialog] = useState(false);

  useEffect(() => {
    // Fetch the creator's username
    const fetchCreatorUsername = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/users/${item.creatorid}`);
        if (response.status === 200) {
          setCreatorUsername(response.data.username);
        } else {
          setCreatorUsername("Unknown");
        }
      } catch (error) {
        console.error("Error fetching creator's details:", error);
        setCreatorUsername("Unknown");
      }
    };

    fetchCreatorUsername();
  }, [item.creatorid]);

  useEffect(() => {
    // Retrieve user details from local storage
    const storedUser = JSON.parse(localStorage.getItem("user"));
    if (storedUser) {
      setUserId(storedUser.userId);
      setIsAdmin(storedUser.role === "ROLE_ADMIN"); // Check if user is an admin
    } else {
      console.error("User details not found in local storage.");
    }
  }, []);

  const handleDelete = async () => {
    console.log("Attempting to delete report ID:", item.reportid);
    try {
      await axios.delete(`http://localhost:8080/api/lostandfound/${item.reportid}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`, // Ensure token is sent
        },
      });
      alert("Item deleted successfully");
      fetchLostItems();
      setOpenDialog(false); // Close the confirmation dialog
    } catch (error) {
      console.error("Error deleting item:", error.response?.data || error.message);
      alert(`Failed to delete item: ${error.response?.data?.message || error.message}`);
    }
  };


  const handleOpenDialog = () => {
    setOpenDialog(true); // Open the dialog when the delete button is clicked
  };

  const handleCloseDialog = () => {
    setOpenDialog(false); // Close the dialog without deleting
  };

  const handleEdit = () => {
    onEdit(item);
  };

  return (
    <Card
      sx={{
        height: 520,
        display: "flex",
        flexDirection: "column",
        boxShadow: 5,
        backgroundImage: item.imageurl
          ? `url(http://localhost:8080${item.imageurl})`
          : `url(http://localhost:8080/images/default_image.jpg)`,
        backgroundSize: "cover",
        backgroundPosition: "center",
        color: "white",
        borderRadius: "8px",
        position: "relative",
      }}
    >
      <Box
        sx={{
          position: "absolute",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: "linear-gradient(to bottom, rgba(0, 0, 0, 0.3) 30%, rgba(0, 0, 0, 1) 100%)", // Gradient transition from image to black
          borderRadius: "8px",
          zIndex: 1, 
        }}
      />
      <CardContent sx={{ flexGrow: 1, overflow: "hidden", position: "relative", zIndex: 10 }}>
        <Stack direction="row" spacing={1} sx={{ mt: 32, mb: 2}}>
          <Chip
            label={item.reporttype === "lost" ? "Lost" : "Found"}
            variant="outlined"
            color={item.reporttype === "lost" ? "error" : "success"}
            sx={{
              fontWeight: "bold",
              fontSize: "16px",
              borderWidth: 3,
              borderColor: item.reporttype === "lost" ? "error.main" : "success.main",
            }}
          />
          <Chip
            label={item.petcategory}
            variant="outlined"
            color="primary"
            sx={{
              fontWeight: "bold",
              borderWidth: 2,
              borderColor: "primary.main",
            }}
          />
        </Stack>
        <Typography color="#7f71f5" fontSize="16px">
          Last Seen
        </Typography>
        <Typography color="secondary" fontWeight="bold" sx={{ ml: 2 }}>
          {item.lastseen}
        </Typography>
        <Typography color="#7f71f5" fontSize="12px">
          Date Reported
        </Typography>
        <Typography color="secondary" fontWeight="bold" sx={{ ml: 2 }}>
          {item.datereported}
        </Typography>
        <Typography
          color="#7f71f5"
          fontStyle="italic"
          sx={{
            whiteSpace: "normal",
            overflowWrap: "break-word",
            mt: 1
          }}
        >
          {item.description}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" sx={{mt: 3}}>
          <Typography color="#7f71f5" fontSize="14px">
            Posted by
          </Typography>
          <Typography
            component={Link}
            to={`/user/${item.creatorid}`} // Link to User.js
            color="secondary"
            fontWeight="bold"
            sx={{
            whiteSpace: "nowrap",
            textDecoration: "none",
            "&:hover": {
              textDecoration: "underline",
              color: "primary.main",
            },
          }}
            >
            {creatorUsername}
          </Typography>
        </Stack>
      </CardContent>

      {/* Show edit and delete buttons for the creator or admin */}
      {(parseInt(userId) === item.creatorid || isAdmin) && (
        <div style={{ position: "fixed", top: 16, right: 16, zIndex: 10 }}>
          <IconButton color="primary" onClick={handleEdit} sx={{ marginRight: 1 }}>
            <EditIcon />
          </IconButton>
          <IconButton
            color="primary"
            onClick={handleOpenDialog}
            disabled={isDeleting}
          >
            <DeleteIcon />
          </IconButton>
        </div>
      )}
      {/* Delete Confirmation Dialog */}
      <Dialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        maxWidth="sm"
        fullWidth
        sx={{
          "& .MuiPaper-root": {
            backgroundColor: "white",
            border: "2px solid",
            borderColor: "red",
            borderRadius: "16px",
            boxShadow: "none",
          },
          "& .MuiDialog-container": {
            backdropFilter: "blur(3px)",
            backgroundColor: "rgba(0, 0, 0, 0.3)",
          },
        }}
      >
        <DialogTitle>
          <Typography
            variant="h4"
            component="div"
            color="error"
            align="center"
            sx={{ fontWeight: "bold", fontFamily: "'Caramel', sans-serif" }}
          >
            Delete Post
          </Typography>
        </DialogTitle>
        <DialogContent
          sx={{
            backgroundColor: "white",
            borderTop: "1px solid",
            borderColor: "error",
            borderRadius: "0 0 16px 16px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            padding: "24px",
            textAlign: "center",
          }}
        >
          <Typography color="error" fontSize="18px" fontWeight="bold" sx={{ whiteSpace: "nowrap", mt: 5 }}>
            Are you sure you want to delete this post?
          </Typography>
          <Typography color="error" fontSize="18px">
            This action cannot be undone.
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} color="secondary">
            <Typography
              variant="h5"
              align="center"
              sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
            >
              Cancel
            </Typography>
          </Button>
          <Button onClick={handleDelete} color="error">
            <Typography
              variant="h5"
              align="center"
              sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
            >
              Delete
            </Typography>
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
};

export default PostCard;
