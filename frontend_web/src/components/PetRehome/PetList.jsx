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
        "http://localhost:8080/api/pet/getAllPets"
      );
      const adoptionResponse = await axios.get(
        "http://localhost:8080/api/adoptions"
      );

      const filteredPets = petResponse.data.filter((pet) => {
        const isInAdoptionProcess = adoptionResponse.data.some((adoption) => {
          console.log("Adoption Status:", adoption.status);
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
    setOpenAuthModal(false); // Close AuthModal
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
        <Typography
          variant="h6"
          component="h6"
          sx={{ color: "#5A20A8", fontWeight: "bold", padding: "20px" }}>
          List of Pets to Adopt
        </Typography>

        <div style={styles.listContainer}>
          {pets.length > 0 ? (
            pets.map((pet) => (
              <Card
                key={pet.petId}
                sx={{
                  width: 360,
                  height: 590,
                  display: "flex",
                  flexDirection: "column",
                  cursor: "pointer",
                }}
                onClick={() => handleCardClick(pet)}>
                {pet.photo ? (
                  <CardMedia
                    component="img"
                    height="140"
                    image={pet.photo}
                    alt={pet.breed}
                    sx={{ objectFit: "cover" }}
                  />
                ) : (
                  <Box
                    sx={{
                      height: 140,
                      backgroundColor: "#f0f0f0",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                    }}>
                    <Typography variant="body2" color="text.secondary">
                      No Image Available
                    </Typography>
                  </Box>
                )}

                <CardContent sx={{ flexGrow: 1, overflow: "hidden" }}>
                  <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
                    <Chip
                      label={pet.type}
                      variant="outlined"
                      color="primary"
                      sx={{
                        fontWeight: "bold",
                        borderWidth: 1.5,
                        borderColor: "primary.main",
                      }}
                    />
                    <Chip
                      label={pet.breed}
                      variant="outlined"
                      color="primary"
                      sx={{
                        fontWeight: "bold",
                        borderWidth: 1.5,
                        borderColor: "primary.main",
                      }}
                    />
                  </Stack>
                  <Typography color="#5A20A8" fontSize="12px" fontWeight="bold">
                    Name
                  </Typography>
                  <Typography color="#5A20A8" fontWeight="bold" sx={{ ml: 2 }}>
                    {pet.name}
                  </Typography>
                  <Typography color="#5A20A8" fontSize="12px" fontWeight="bold">
                    Pet Type
                  </Typography>
                  <Typography color="#5A20A8" fontWeight="bold" sx={{ ml: 2 }}>
                    {pet.type}
                  </Typography>
                  <Typography color="#5A20A8" fontSize="12px" fontWeight="bold">
                    Breed
                  </Typography>
                  <Typography color="#5A20A8" fontWeight="bold" sx={{ ml: 2 }}>
                    {pet.breed}
                  </Typography>
                  <Typography color="#5A20A8" fontSize="12px" fontWeight="bold">
                    Age
                  </Typography>
                  <Typography color="#5A20A8" fontWeight="bold" sx={{ ml: 2 }}>
                    {pet.age} years
                  </Typography>
                  <Typography color="#5A20A8" fontSize="12px" fontWeight="bold">
                    Gender
                  </Typography>
                  <Typography color="#5A20A8" fontWeight="bold" sx={{ ml: 2 }}>
                    {pet.gender}
                  </Typography>
                  <br />
                  <Typography
                    color="#5A20A8"
                    fontStyle="italic"
                    fontWeight="bold"
                    noWrap>
                    {pet.description}
                  </Typography>
                </CardContent>
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
    flexDirection: "column",
    alignItems: "flex-start",
    minHeight: "100vh",
  },
  listContainer: {
    display: "flex",
    flexWrap: "wrap",
    gap: "20px",
    justifyContent: "flex-start",
    padding: "10px",
  },
};

export default PetList;
