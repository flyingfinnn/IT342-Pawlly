import React, { useState, useEffect } from "react";
import {
  Container,
  Grid,
  Typography,
  TextField,
  Button,
  Box,
} from "@mui/material";
import axios from "axios";
import CreatePostDialog from "./CreatePostDialog";
import PostCard from "./PostCard";
import SearchIcon from "@mui/icons-material/Search";

const NewsFeed = () => {
  const [openDialog, setOpenDialog] = useState(false);
  const [lostItems, setLostItems] = useState([]);
  const [postToEdit, setPostToEdit] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchQuery, setSearchQuery] = useState(""); // State for search input

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

  const filteredItems = lostItems.filter(
    (item) =>
      item.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.reporttype.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.petcategory.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <>
      <Box
        sx={{
          position: "fixed",
          top: "90px",
          left: "70%",
          transform: "translateX(-50%)",
          zIndex: 1300,
          width: "80%",
          maxWidth: "400px",
          padding: "5px",
        }}
      >
        <TextField
          variant="outlined"
          placeholder="Search"
          value={searchQuery}
          onChange={handleSearchChange}
          sx={{
            width: "100%",
            backgroundColor: "transparent",

            "& .MuiOutlinedInput-root": {
              height: "35px",
              border: "1px solid white",
              borderRadius: "25px",
              "&.Mui-focused": {
                borderColor: "white",
              },
            },
            "& .MuiOutlinedInput-input": {
              color: "white",
            },
            "& .MuiSvgIcon-root": {
              color: "white",
            },
            "& fieldset": {
              border: "none",
            },
          }}
          InputProps={{
            endAdornment: (
              <Button
                sx={{ padding: 0, backgroundColor: "transparent !important" }}
              >
                {" "}
                <SearchIcon />
              </Button>
            ),
          }}
        />
      </Box>

      {/* Main Content */}
      <Container 
        sx={{ 
          mt: 5, 
          maxWidth: '90% !important', // Span the full width of the viewport
          padding: '0 100px' // Optional padding for spacing 
        }}
      >
        <Grid
          container
          justifyContent="space-between"
          alignItems="center"
          maxWidth="lg"
          sx={{ mb: 2 }}
        >
          <Typography
            variant="h2"
            component="div"
            color="primary"
            sx={{ fontWeight: "bold", fontFamily: "'Caramel', sans-serif" }}
          >
            Lost And Found
          </Typography>
        </Grid>

        <Grid
          container
          justifyContent="center"
          rows={2}
          sx={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            textAlign: "center",
          }}
        >
          <TextField
            variant="outlined"
            fullWidth
            height="5px"
            rows={2}
            placeholder="Have you found someone's pet? Or did you lose a pet?"
            onClick={handleTextFieldClick}
            sx={{
              maxWidth: 980,
              backgroundColor: "transparent !important",
              borderRadius: "8px",
              "& .MuiOutlinedInput-root": {
                "& .MuiOutlinedInput-notchedOutline": {
                  borderColor: "primary.main",
                },
                "&:hover .MuiOutlinedInput-notchedOutline": {
                  borderColor: "primary.dark",
                },
                "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                  borderColor: "primary.light",
                },
              },
            }}
          />
        </Grid>

        <CreatePostDialog
          open={openDialog}
          setOpen={setOpenDialog}
          fetchLostItems={fetchLostItems}
          postToEdit={postToEdit}
          isEditing={isEditing}
          setIsEditing={setIsEditing}
          setPostToEdit={setPostToEdit}
        />

      <Grid container spacing={2} justifyContent="center" sx={{ mt: 2 }}>
        {filteredItems.map((item) => (
          <Grid
            item
            key={item.reportid}
            xs={12}
            sm={6}
            md={6}
            lg={6}
            sx={{
              display: "flex",
              justifyContent: "center",
            }}
          >
            <Box
              sx={{
                maxWidth: 720, // Adjust based on desired card size
                width: "100%", // Ensures responsiveness
              }}
            >
              <PostCard
                item={item}
                fetchLostItems={fetchLostItems}
                onEdit={handleEditPost}
              />
            </Box>
          </Grid>
        ))}
      </Grid>

      </Container>
    </>
  );
};

export default NewsFeed;