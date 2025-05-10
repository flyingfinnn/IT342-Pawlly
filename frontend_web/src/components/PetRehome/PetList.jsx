import React, { useState, useEffect } from "react";
import {
  Card,
  Box,
  CardContent,
  CardMedia,
  Typography,
  Stack,
  Chip,
  ToggleButton,
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import AdoptionForm from '../Adoption/AdoptionForm';
import RehomeForm from '../PetRehome/RehomeForm';
import axios from "axios";
import { useUser } from '../UserContext';
import AuthModal from '../AuthModal';

const SUPABASE_BUCKET_URL = "https://<your-project-ref>.supabase.co/storage/v1/object/public/<bucket-name>/";

function getPetPhotos(pet) {
  // If photo is present, use it
  if (pet.photo) return [pet.photo];
  // Otherwise, check photo1-photo4
  const photoFields = [pet.photo1, pet.photo2, pet.photo3, pet.photo4];
  return photoFields
    .filter(Boolean)
    .map(path => path.startsWith("http") ? path : SUPABASE_BUCKET_URL + path);
}

const PetList = ({ onPetAdded }) => {
  const { user } = useUser();
  const [openAdoption, setOpenAdoption] = useState(false);
  const [openRehome, setOpenRehome] = useState(false);
  const [selectedPet, setSelectedPet] = useState(null);
  const [pets, setPets] = useState([]);
  const [openAuthModal, setOpenAuthModal] = useState(false);

  useEffect(() => {
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    try {
      const petResponse = await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/pet/getAllPets`
      );
      const adoptionResponse = await axios.get(
        `${process.env.REACT_APP_BACKEND_URL}/api/adoptions`
      );
  
      const petData = Array.isArray(petResponse.data)
        ? petResponse.data
        : Array.isArray(petResponse.data.data)
          ? petResponse.data.data
          : [];
  
      const adoptionData = Array.isArray(adoptionResponse.data)
        ? adoptionResponse.data
        : Array.isArray(adoptionResponse.data.data)
          ? adoptionResponse.data.data
          : [];
  
      const filteredPets = petData.filter((pet) => {
        const isInAdoptionProcess = adoptionData.some((adoption) => {
          return (
            (adoption.status === "PENDING" || adoption.status === "APPROVED") &&
            adoption.breed === pet.breed &&
            adoption.petType === pet.type &&
            adoption.description === pet.description
          );
        });
  
        return pet.status === "ACCEPTED_REHOME" && !isInAdoptionProcess;
      });
  
      setPets(filteredPets);
    } catch (error) {
      console.error("Failed to fetch updated PetList", error);
    }
  };    

  const handleNewPet = (newPet) => {
    setPets((prevPets) => [...prevPets, newPet]);
  };

  const handleAuthClose = () => {
    setOpenAuthModal(false); 
  };

  const handleCardClick = (pet) => {
    if (user) {
        setSelectedPet(pet);
        setOpenAdoption(true);
    } else {
        alert("You must be logged in to adopt a pet.");
        setOpenAuthModal(true);
    }
  };

  const handleAdoptionClose = () => {
    setOpenAdoption(false);
    setSelectedPet(null);
  };

  const handleRehomeClick = () => {
    setOpenRehome(true);
  };

  const handleRehomeClose = () => {
    setOpenRehome(false);
  };

  return (
    <>
      <div style={styles.pageContainer}>
        <Box sx={{ width: "100%", display: "flex", justifyContent: "center" }}>
            <Typography
              variant="h5"
              component="h1"
              sx={{
                color: '#5A20A8',
                fontWeight: 'bold',
                mt: '30px',
                mb: '8px',
                fontSize: '1.75rem',
                textAlign: 'center',
              }}
            >
              Find Your New Best Friend — Adopt a Pet Today!
            </Typography>
          </Box>

        <div style={styles.listContainer}>
          {pets.length > 0 ? (
            pets.map((pet) => (
              <Card
                key={pet.petId}
                sx={{
                  width: 320,
                  height: "auto",
                  display: "flex",
                  flexDirection: "column",
                  borderRadius: 4,
                  boxShadow: 3,
                  transition: "transform 0.2s ease, box-shadow 0.3s ease",
                  "&:hover": {
                    transform: "scale(1.02)",
                    boxShadow: 6,
                  },
                }}
              >
                {getPetPhotos(pet).length === 0 ? (
                  <Box
                    sx={{
                      height: 180,
                      backgroundColor: "#f0f0f0",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      borderTopLeftRadius: 16,
                      borderTopRightRadius: 16,
                    }}
                  >
                    <Typography variant="body2" color="text.secondary">
                      No Image Available
                    </Typography>
                  </Box>
                ) : (
                  <CardMedia
                    component="img"
                    height="180"
                    image={getPetPhotos(pet)[0]}
                    alt={pet.breed}
                    sx={{ objectFit: "cover", borderTopLeftRadius: 16, borderTopRightRadius: 16 }}
                  />
                )}

                <CardContent sx={{ flexGrow: 1, p: 2 }}>
                  <Stack direction="row" spacing={1} sx={{ mb: 1 }}>
                    <Chip label={pet.type} color="secondary" size="small" />
                    <Chip label={pet.breed} variant="outlined" size="small" />
                  </Stack>

                  <Typography variant="subtitle2" color="text.secondary">
                    Name
                  </Typography>
                  <Typography variant="body1" fontWeight="bold" gutterBottom>
                    {pet.name}
                  </Typography>

                  <Typography variant="subtitle2" color="text.secondary">
                    Age & Gender
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    {pet.age} years | {pet.gender}
                  </Typography>

                  <Typography variant="subtitle2" color="text.secondary">
                    Description
                  </Typography>
                  <Typography
                    variant="body2"
                    color="text.primary"
                    sx={{
                      fontStyle: "italic",
                      overflow: "hidden",
                      display: "-webkit-box",
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: "vertical",
                    }}
                  >
                    {pet.description}
                  </Typography>
                </CardContent>

                <Box sx={{ p: 2, pt: 0, mt: "auto" }}>
                  <ToggleButton
                    value="adopt"
                    fullWidth
                    onClick={() => handleCardClick(pet)}
                    sx={{
                      borderRadius: "25px",
                      border: "2px solid",
                      borderColor: "#685ccc",
                      backgroundColor: "#685ccc",
                      color: "#fff",
                      "&:hover": {
                        backgroundColor: "white",
                        color: "#685ccc",
                      },
                    }}
                  >
                    Adopt Pet
                  </ToggleButton>
                </Box>
              </Card>
            ))
          ) : (
            <Typography variant="body1" color="#5A20A8" fontWeight="bold">
              No pets available for adoption at the moment.
            </Typography>
          )}
        </div>
      </div>

      <Dialog
        open={openAdoption}
        onClose={handleAdoptionClose}
        fullWidth
        maxWidth="md">
        <DialogTitle>
          <IconButton
            aria-label="close"
            onClick={handleAdoptionClose}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <AdoptionForm pet={selectedPet} />
        </DialogContent>
      </Dialog>

      <Dialog
        open={openRehome}
        onClose={handleRehomeClose}
        fullWidth
        maxWidth="md">
        <DialogTitle>
          <IconButton
            aria-label="close"
            onClick={handleRehomeClose}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <RehomeForm />
        </DialogContent>
      </Dialog>
      
      {user && (
      <Box
        sx={{
          width: "100vw",
          backgroundColor: "#6c5ce7",
          color: "white",
          textAlign: "center",
          padding: "8px 0",
          position: "fixed",
          bottom: 0,
          left: 0,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
        }}>
          
        <Typography variant="h7" fontWeight="bold" sx={{ mr: 2 }}>
          Do you want to rehome your pet?
        </Typography>
        
          <ToggleButton
            onClick={handleRehomeClick}
            sx={{
              border: "2px solid",
              borderRadius: "25px",
              padding: "12px 36px",
              borderColor: "#6c5ce7",
              backgroundColor: "#6c5ce7",
              color: "#fff",
              "&:hover": {
                backgroundColor: "white",
                color: "#6c5ce7",
              },
            }}>
            Rehome
          </ToggleButton>
        
      </Box>
    )}
    <AuthModal open={openAuthModal} handleClose={handleAuthClose} />
    </>
  );
};

const styles = {
  pageContainer: {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-start",
    minHeight: "100vh",
    paddingBottom: "70px", // extra space for footer
  },
  listContainer: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
    gap: "20px",
    justifyContent: "center",
    width: "100%",
    padding: "10px",
  },
};


export default PetList;
