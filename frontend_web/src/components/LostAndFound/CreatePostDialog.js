import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  TextField,
  Button,
  ToggleButton,
  ToggleButtonGroup,
  Box,
  Typography,
} from "@mui/material";
import axios from "axios";

const CreatePostDialog = ({
  open,
  setOpen,
  fetchLostItems,
  postToEdit,
  isEditing,
}) => {
  const [formData, setFormData] = useState({
    reporttype: "lost",
    datereported: "",
    lastseen: "",
    description: "",
    imagedata: null,
  });

  const [previewImage, setPreviewImage] = useState(null); // Move this inside the component
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    // Retrieve user ID from local storage
    const storedUser = JSON.parse(localStorage.getItem("user"));
    if (storedUser && storedUser.userId) {
      setUserId(storedUser.userId);
    } else {
      console.error("User ID not found in local storage.");
    }
  }, []);

  useEffect(() => {
    if (isEditing && postToEdit) {
      setFormData({
        reporttype: postToEdit.reporttype || "lost",
        petcategory: postToEdit.petcategory || "",
        datereported: postToEdit.datereported || "",
        lastseen: postToEdit.lastseen || "",
        description: postToEdit.description || "",
        imagedata: null,
      });

      if (postToEdit.imageurl) {
        setPreviewImage(postToEdit.imageurl);
      }
    } else {
      setFormData({
        reporttype: "lost",
        petcategory: "",
        datereported: "",
        lastseen: "",
        description: "",
        imagedata: null,
      });
      setPreviewImage(null);
    }
  }, [isEditing, postToEdit, open]);
  
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file && file.type.startsWith("image/")) {
      setFormData({ ...formData, imagedata: file });
      const previewUrl = URL.createObjectURL(file);
      setPreviewImage(previewUrl);
    } else {
      alert("Please upload a valid image file.");
    }
  };

  const handleReportTypeChange = (event, newReportType) => {
    if (newReportType) {
      setFormData({ ...formData, reporttype: newReportType });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!userId) {
      alert("You must be logged in to create or edit a post.");
      return;
    }

    const token = localStorage.getItem("token"); // Ensure token exists
    if (!token) {
      console.error("JWT token not found");
      alert("Please log in to submit a post.");
      return;
    }

    const dataToSubmit = new FormData();
    dataToSubmit.append("reporttype", formData.reporttype || "lost");
    dataToSubmit.append("petcategory", formData.petcategory);
    dataToSubmit.append("datereported", formData.datereported);
    dataToSubmit.append("lastseen", formData.lastseen);
    dataToSubmit.append("description", formData.description);
    dataToSubmit.append("creatorid", userId);

    if (formData.imagedata) {
      dataToSubmit.append("imagefile", formData.imagedata);
    }

    try {
      const url = isEditing
        ? `http://localhost:8080/api/lostandfound/${postToEdit.reportid}`
        : `http://localhost:8080/api/lostandfound`;

      const method = isEditing ? "put" : "post";
      const response = await axios[method](url, dataToSubmit, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      console.log(isEditing ? "Report updated" : "Report created", response.data);
      setOpen(false);
      window.location.reload();
    } catch (error) {
      console.error("Submission error:", error.response?.data || error.message);

      const errorDetails = error.response?.data
        ? JSON.stringify(error.response.data, null, 2)
        : error.message;

      alert(`Failed to submit the post. Server response:\n${errorDetails}`);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
      maxWidth="md" // Make dialog width larger
      fullWidth
      sx={{
        "& .MuiPaper-root": {
          backgroundColor: "white",
          border: "2px solid",
          borderColor: "primary.main",
          borderRadius: "16px",
          boxShadow: "none",
          padding: "30px", // Increased padding for the dialog
        },
        "& .MuiDialog-container": {
          backdropFilter: "blur(3px)",
          backgroundColor: "rgba(0, 0, 0, 0.3)",
        },
      }}
    >
      <DialogTitle>
        <Typography
          variant="h3" // Larger font size for title
          align="center"
          color="primary"
          sx={{ fontWeight: "bold", fontFamily: "'Caramel', sans-serif" }}
        >
          {isEditing ? "Edit Post" : "Create Post"}
        </Typography>
      </DialogTitle>

      <DialogContent
        sx={{
          backgroundColor: "white",
          borderTop: "1px solid",
          borderColor: "primary.main",
          borderRadius: "0 0 16px 16px",
          padding: "24px",
        }}
      >

        {/* Main Container */}
        <Grid container spacing={3}
           sx = {{
            mt: 2
          }}
        >
          {/* Left Side (Lost and Found toggle & image upload) */}
          <Grid item xs={12} sm={6} md={6}> {/* Increased width */}
            <Box 
              sx={{
                display: "flex",         // Set display to flex
                flexDirection: "column", // Align items vertically in a column
                justifyContent: "center", // Center items horizontally
                alignItems: "center",    // Center items vertically
                height: "100%",
                }}
            > 
              <ToggleButtonGroup
                value={formData.reporttype || "lost"}
                exclusive
                onChange={handleReportTypeChange}
                aria-label="Report Type"
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  mb: 2,
                  mt: 2,
                  fontSize: "1.2rem", // Larger font size for the toggle buttons
                }}
              >
                <ToggleButton
                  value="lost"
                  aria-label="Lost"
                  sx={{
                    border: "2px solid",
                    borderRadius: "8px",
                    padding: "16px 40px", // Increased padding
                    borderColor:
                      formData.reporttype === "lost" ? "primary.main" : "grey.500",
                    color: formData.reporttype === "lost" ? "#fff" : "grey.500",
                    backgroundColor:
                      formData.reportType === "lost"
                        ? "primary.main"
                        : "transparent",
                    "&.Mui-selected": {
                      backgroundColor: "red",
                      color: "#fff",
                    },
                    "&:hover": {
                      backgroundColor:
                        formData.reporttype === "lost"
                          ? "primary.dark"
                          : "rgba(0, 0, 0, 0.04)",
                    },
                  }}
                >
                  <Typography
                    variant="h5"
                    align="center"
                    sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
                  >
                    Lost
                  </Typography>
                </ToggleButton>
                <ToggleButton
                  value="found"
                  aria-label="Found"
                  sx={{
                    border: "2px solid",
                    borderRadius: "8px",
                    padding: "16px 40px", // Increased padding
                    borderColor:
                      formData.reporttype === "found" ? "primary.main" : "grey.500",
                    color: formData.reporttype === "found" ? "#fff" : "grey.500",
                    backgroundColor:
                      formData.reporttype === "found"
                        ? "primary.main"
                        : "transparent",
                    "&.Mui-selected": {
                      backgroundColor: "green",
                      color: "#fff",
                    },
                    "&:hover": {
                      backgroundColor:
                        formData.reporttype === "found"
                          ? "primary.dark"
                          : "rgba(0, 0, 0, 0.04)",
                    },
                  }}
                >
                  <Typography
                    variant="h5"
                    align="center"
                    sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
                  >
                    Found
                  </Typography>
                </ToggleButton>
              </ToggleButtonGroup>
              
              {/* Image Upload */}
              <Box
                sx={{
                  width: "400px",
                  height: "300px",
                  border: isEditing ? "none" : "5px dotted #675BC8",
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  borderRadius: "8px",
                  overflow: "hidden",
                  position: "relative",
                }}
                onClick={
                  isEditing
                    ? undefined // Disable click action during editing
                    : () => document.getElementById("image-upload").click()
                }
              >
                {isEditing && postToEdit.imageurl ? (
                  // Display the existing image if editing
                  <img
                    src={`http://localhost:8080${postToEdit.imageurl}`}
                    alt="Existing Post"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                      borderRadius: "8px",
                    }}
                  />
                ) : previewImage ? (
                  // Display the preview image if available
                  <img
                    src={previewImage}
                    alt="Preview"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                      borderRadius: "8px",
                    }}
                  />
                ) : (
                  // Placeholder for new uploads
                  <Typography
                    variant="h4"
                    color="textSecondary"
                    sx={{ fontWeight: "bold", position: "absolute" }}
                  >
                    +
                  </Typography>
                )}
                {!isEditing && (
                  // File input only appears when not editing
                  <input
                    id="image-upload"
                    type="file"
                    accept="image/*"
                    style={{ display: "none" }}
                    onChange={handleImageUpload}
                  />
                )}
              </Box>
            </Box>
          </Grid>

          {/* Right Side (form fields) */}
          <Grid item xs={12} sm={6} md={6}> {/* Increased width */}
            <Box
              sx={{
                display: "flex",         // Set display to flex
                flexDirection: "column", // Align items vertically in a column
                justifyContent: "center", // Center items horizontally
                alignItems: "center",    // Center items vertically
                height: "100%",
                }}
            >
            <TextField
              label="Date Reported"
              type="date"
              name="datereported"
              value={formData.datereported}
              onChange={handleInputChange}
              fullWidth
              InputLabelProps={{
                shrink: true,
                style: {
                  marginBottom: '8px', // Adjust the margin to move the label down
                },
              }}
              margin="normal"
              sx={{
                fontSize: "1.2rem", // Larger font size for text field
                padding: "10px", // Increased padding
              }}
            />
            <TextField
              label="Last Seen"
              name="lastseen"
              value={formData.lastseen}
              onChange={handleInputChange}
              fullWidth
              margin="normal"
              InputLabelProps={{
                shrink: true,
                style: {
                  marginBottom: '8px', // Adjust the margin to move the label down
                },
              }}
              sx={{
                fontSize: "1.2rem", // Larger font size for text field
                padding: "10px", // Increased padding
              }}
            />
            <TextField
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              fullWidth
              multiline
              rows={4}
              margin="normal"
              InputLabelProps={{
                shrink: true,
                style: {
                  marginBottom: '8px', // Adjust the margin to move the label down
                },
              }}
              sx={{
                fontSize: "1.2rem", // Larger font size for text field
                padding: "10px", // Increased padding
              }}
            />
            </Box>
          </Grid>
        </Grid>
      </DialogContent>

      <DialogActions>
          <Button onClick={() => setOpen(false)} color="error">
            <Typography
              variant="h5"
              align="center"
              sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
            >
              Cancel
            </Typography>
          </Button>
          <Button onClick={handleSubmit} color="primary">
            <Typography
              variant="h5"
              align="center"
              sx={{ fontWeight: "regular", fontFamily: "'Caramel', sans-serif" }}
            >
              CONFIRM
            </Typography>
          </Button>
        </DialogActions>
    </Dialog>
  );
};

export default CreatePostDialog;
