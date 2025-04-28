import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    IconButton,
    Button,
    Snackbar,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Select,
    MenuItem,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { useNavigate } from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';


const AdoptionList = () => {
    const [adoptions, setAdoptions] = useState([]);
    const [rehomes, setRehomes] = useState([]); // State to store rehome records
    const [editRehome, setEditRehome] = useState(null);
    const [editAdoption, setEditAdoption] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const [error, setError] = useState('');
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [deleteId, setDeleteId] = useState(null);
    const navigate = useNavigate();

    // Fetch adoption and rehome records
    useEffect(() => {
        const fetchRecords = async () => {
            try {
                const adoptionResponse = await axios.get('http://localhost:8080/api/adoptions');
                const rehomeResponse = await axios.get('http://localhost:8080/api/pet/getAllPets'); 
                setAdoptions(adoptionResponse.data);
                setRehomes(rehomeResponse.data); // Store rehome data
            } catch (error) {
                setError("Failed to load records.");
            }
        };

        fetchRecords();
    }, []);

    const handleBack = () => {
        navigate('/adopt'); 
    };

    const handleDeleteClick = (id) => {
        setDeleteId(id);
        setDeleteDialogOpen(true);
    };

    const handleDelete = async () => {
        setDeleteDialogOpen(false);
        if (!deleteId) return;

        try {
            await axios.delete(`http://localhost:8080/api/adoptions/${deleteId}`);
            setAdoptions(prevAdoptions => prevAdoptions.filter(adoption => adoption.adoptionID !== deleteId));
            setSuccessMessage("Adoption deleted successfully!");
            setDeleteId(null);
        } catch (error) {
            setError("Failed to delete adoption.");
        }
    };

    const handleEditAdoption = () => {
        setConfirmDialogOpen(true);
    };

    const handleEditRehome = async () => {
        setEditDialogOpen(false);
    
        if (!editRehome) return;
    
        try {
            const response = await axios.put(`http://localhost:8080/api/pet/updateRehomeStatus/${editRehome.pid}`, editRehome);

            setRehomes((prevRehomes) =>
                prevRehomes.map((rehome) =>
                    rehome.pid === response.data.pid ? response.data : rehome
                )
            );
    
            setEditRehome(null); // Clear the selected record
            setSuccessMessage("Rehome status updated successfully!");
        } catch (error) {
            setError("Failed to update rehome status.");
        }
    };
    
    
    const handleConfirmUpdate = async () => {
        setConfirmDialogOpen(false);
        if (!editAdoption) return;

        try {
            const response = await axios.put(`http://localhost:8080/api/adoptions/${editAdoption.adoptionID}`, editAdoption);
            setAdoptions(prevAdoptions => prevAdoptions.map(adoption => (adoption.adoptionID === response.data.adoptionID ? response.data : adoption)));
            setEditDialogOpen(false);
            setEditAdoption(null);
            setSuccessMessage("Adoption updated successfully!");
        } catch (error) {
            setError("Failed to update adoption.");
        }
    };

    const handleEditClick = (adoption) => {
        setEditAdoption(adoption);
        setEditDialogOpen(true);
    };
    
    const handleEditRehomeClick = (rehome) => {
        setEditRehome(rehome);
        setEditDialogOpen(true);
    };
    

    const renderAdoptionCards = (adoptionList) => (
        adoptionList.map((adoption) => (
            <Grid item xs={12} sm={6} md={5} key={adoption.adoptionID}>
                <Card style={styles.card}>
                    <CardContent>
                        <Typography variant="h6">ID: {adoption.adoptionID}</Typography>
                        <Typography variant="body1">Name: {adoption.name}</Typography>
                        <Typography variant="body1">Address: {adoption.address}</Typography>
                        <Typography variant="body1">Contact Number: {adoption.contactNumber}</Typography>
                        <Typography variant="body1">Pet Type: {adoption.petType}</Typography>
                        <Typography variant="body1">Breed: {adoption.breed}</Typography>
                        <Typography variant="body1">Description: {adoption.petDescription}</Typography>
                        <Typography variant="body1">Submission Date: {adoption.submissionDate}</Typography>
                        <Typography variant="body1">Status: {adoption.status}</Typography>
                        <div style={styles.buttonContainer}>
                            <IconButton color="primary" onClick={() => handleEditClick(adoption)}>
                                <EditIcon />
                            </IconButton>
                            <IconButton color="primary" onClick={() => handleDeleteClick(adoption.adoptionID)}>
                                <DeleteIcon />
                            </IconButton>
                        </div>
                    </CardContent>
                </Card>
            </Grid>
        ))
    );

    const renderRehomeCards = (rehomeList) => (
        rehomeList.map((rehome) => (
            <Grid item xs={12} sm={6} md={5} key={rehome.pid}>
                <Card style={styles.card}>
                    <CardContent>
                        <Typography variant="h6">ID: {rehome.pid}</Typography>
                        <Typography variant="body1">Name: {rehome.name}</Typography>
                        <Typography variant="body1">Type: {rehome.type}</Typography>
                        <Typography variant="body1">Breed: {rehome.breed}</Typography>
                        <Typography variant="body1">Age: {rehome.age}</Typography>
                        <Typography variant="body1">Status: {rehome.status}</Typography>
                        <div style={styles.buttonContainer}>
                        <IconButton color="primary" onClick={() => handleEditRehomeClick(rehome)}>
                            <EditIcon /> 
                        </IconButton>
                            <IconButton color="primary">
                                <DeleteIcon />
                            </IconButton>
                        </div>
                    </CardContent>
                </Card>
            </Grid>
        ))
    );

    return (
        <div style={styles.container}>
            <IconButton onClick={handleBack} style={styles.backButton}>
                <ArrowBackIcon />
            </IconButton>
            <Typography variant="h4" sx={{ fontFamily: "'Caramel', sans-serif", fontWeight: "bold", color: '#5A20A8' }}>
                Adoption and Rehome List
            </Typography>
            {error && <Typography variant="body1" sx={{ color: 'red' }}>{error}</Typography>}

            <Grid container spacing={4}>
                {/* Left Column for Adoptions */}
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" sx={styles.centeredLeftHeading}>Pending Adoptions</Typography>
                    <Grid container spacing={2}>
                        {renderAdoptionCards(adoptions.filter(adoption => adoption.status === 'PENDING'))}
                    </Grid>

                    <Typography variant="h6" sx={styles.centeredLeftHeading}>Approved Adoptions</Typography>
                    <Grid container spacing={2}>
                        {renderAdoptionCards(adoptions.filter(adoption => adoption.status === 'APPROVED'))}
                    </Grid>

                    <Typography variant="h6" sx={styles.centeredLeftHeading}>Rejected Adoptions</Typography>
                    <Grid container spacing={2}>
                        {renderAdoptionCards(adoptions.filter(adoption => adoption.status === 'REJECTED'))}
                    </Grid>
                    <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
                <DialogTitle>Edit Adoption Status</DialogTitle>
                <DialogContent>
                    {editAdoption && (
                        <Select
                            label="Status"
                            value={editAdoption.status}
                            onChange={(e) => setEditAdoption({ ...editAdoption, status: e.target.value })}
                            variant="outlined"
                            fullWidth
                            sx={{ marginBottom: 2 }}
                        >
                            <MenuItem value="APPROVED">Approved</MenuItem>
                            <MenuItem value="REJECTED">Rejected</MenuItem>
                        </Select>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleEditAdoption} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={confirmDialogOpen} onClose={() => setConfirmDialogOpen(false)}>
                <DialogTitle>Confirm Update</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to update this record?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setConfirmDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleConfirmUpdate} variant="contained" color="primary">Do it</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to delete this record?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleDelete} variant="contained" color="primary">Delete</Button>
                </DialogActions>
            </Dialog>
                </Grid>

                {/* Right Column for Rehome */}
                <Grid item xs={12} md={6}>
                    <Typography variant="h6" sx={styles.centeredRightHeading}>Pending Rehome</Typography>
                    <Grid container spacing={2}>
                        {renderRehomeCards(rehomes.filter(rehome => rehome.status === 'PENDING_REHOME'))}
                    </Grid>

                    <Typography variant="h6" sx={styles.centeredRightHeading}>Accepted Rehome</Typography>
                    <Grid container spacing={2}>
                        {renderRehomeCards(rehomes.filter(rehome => rehome.status === 'ACCEPTED_REHOME'))}
                    </Grid>
                    <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
                <DialogTitle>Edit Rehome Status</DialogTitle>
                <DialogContent>
                    {editRehome && (
                        <Select
                            label="Status"
                            value={editRehome.status}
                            onChange={(e) => setEditRehome({ ...editRehome, status: e.target.value })}
                            variant="outlined"
                            fullWidth
                            sx={{ marginBottom: 2 }}
                        >
                            <MenuItem value="ACCEPTED_REHOME">Accepted</MenuItem>
                            <MenuItem value="REJECTED_REHOME">Rejected</MenuItem>
                        </Select>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
                    <Button onClick={handleEditRehome} variant="contained">Save</Button>
                </DialogActions>
            </Dialog>

            {/* Delete Confirmation Dialog */}
            <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
                <DialogTitle>Confirm Delete</DialogTitle>
                    <DialogContent>
                        <Typography>Are you sure you want to delete this record?</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
                        <Button onClick={handleDelete} variant="contained" color="primary">Delete</Button>
                    </DialogActions>
            </Dialog>
                </Grid>
            </Grid>

            <Snackbar open={!!successMessage} autoHideDuration={6000} onClose={() => setSuccessMessage('')} message={successMessage} />

        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        padding: '20px',
    },
    backButton: {
        alignSelf: 'flex-start', 
    },
    card: {
        width: '300px',
        minHeight: '250px',
        margin: 'auto',
        marginBottom: '20px',
        padding: '10px',
    },
    buttonContainer: {
        marginTop: '10px',
        display: 'flex',
        justifyContent: 'flex-end',
    },
    centeredLeftHeading: {
        textAlign: 'left',
        paddingLeft: '30%',
        fontFamily: "'Arial', sans-serif",
        fontWeight: 'bold',
        color: '#5A20A8',
        marginBottom: '10px',
    },
    centeredRightHeading: {
        textAlign: 'right',
        paddingRight: '40%',
        fontFamily: "'Arial', sans-serif",
        fontWeight: 'bold',
        color: '#5A20A8',
        marginBottom: '10px',
    },
};

export default AdoptionList;
