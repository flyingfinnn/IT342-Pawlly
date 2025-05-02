import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    IconButton,
    Snackbar,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Select,
    MenuItem,
    Button,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const PetDashboard = ({ onPetAdded = () => {} }) => {
    const [rehomes, setRehomes] = useState([]); 
    const [editRehome, setEditRehome] = useState(null);
    const [newStatus, setNewStatus] = useState(''); 
    const [successMessage, setSuccessMessage] = useState(''); 
    const [error, setError] = useState(''); 
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false); 
    const [deleteRehomeId, setDeleteRehomeId] = useState(null);

    // Fetch rehome records
    useEffect(() => {
        const fetchRehomes = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/pet/getAllPets');
                setRehomes(response.data);
            } catch (error) {
                setError('Failed to load rehome records.');
            }
        };

        fetchRehomes();
    }, []);

    const handleEditRehomeClick = (rehome) => {
        setEditRehome(rehome); 
        setNewStatus(rehome.status); 
        setEditDialogOpen(true); 
    };

    const handleStatusChange = (event) => {
        setNewStatus(event.target.value);
    };

    const handleDialogClose = () => {
        setEditDialogOpen(false);
        setConfirmDialogOpen(false); 
        setEditRehome(null);
        setDeleteRehomeId(null); 
    };

    const handleSaveRehomeStatus = async () => {
        try {
            if (editRehome) {
                const petToUpdate = {
                    ...editRehome, 
                    status: newStatus 
                };

                if (newStatus === 'REJECTED') {
                    await axios.delete(`http://localhost:8080/api/pet/deletePetDetails/${editRehome.pid}`);
                    setRehomes((prev) => prev.filter((rehome) => rehome.pid !== editRehome.pid));
                    setSuccessMessage('Rehome record rejected and deleted.');
                } else if (newStatus === 'ACCEPTED_REHOME') {
                    await axios.put(`http://localhost:8080/api/pet/putPetDetails`, petToUpdate, {
                        params: {
                            pid: editRehome.pid 
                        }
                    });

                    setRehomes(prev => 
                        prev.map(rehome => 
                            rehome.pid === editRehome.pid 
                                ? {...rehome, status: 'ACCEPTED_REHOME'} 
                                : rehome
                        )
                    );
                    setSuccessMessage('Rehome approved.');
                    onPetAdded(petToUpdate); 
                }
            }
        } catch (error) {
            console.error('Error updating rehome status:', error.response?.data || error.message);
            setError('Failed to update rehome status. ' + (error.response?.data?.message || error.message));
        } finally {
            handleDialogClose();
        }
    };

    const handleDeleteRehomeClick = (rehomeId) => {
        setDeleteRehomeId(rehomeId);
        setConfirmDialogOpen(true); 
    };

    const handleDeleteRehome = async () => {
        try {
            await axios.delete(`http://localhost:8080/api/pet/deletePetDetails/${deleteRehomeId}`);
            setRehomes((prev) => prev.filter((rehome) => rehome.pid !== deleteRehomeId));
            setSuccessMessage('Rehome record deleted.');
        } catch (error) {
            setError('Failed to delete rehome record.');
        } finally {
            handleDialogClose();
        }
    };

    const renderRehomeCards = (rehomeList) => (
        rehomeList.map((rehome) => (
            <Grid item xs={12} sm={6} md={3} key={rehome.pid}>
                <Card style={styles.card}>
                    <CardContent>
                        <Typography variant="h6" style={styles.textShade}>ID: {rehome.pid}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Name: {rehome.name}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Type: {rehome.type}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Breed: {rehome.breed}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Age: {rehome.age}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Gender: {rehome.gender}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Description: {rehome.description}</Typography>
                        <Typography variant="body1" style={styles.textShade}>User Name: {rehome.userName}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Address: {rehome.address}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Contact Number: {rehome.contactNumber}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Submission Date: {rehome.submissionDate || 'N/A'}</Typography>
                        <Typography variant="body1" style={styles.textShade}>Status: {rehome.status}</Typography>

                        {rehome.photo && (
                            <div style={styles.imageContainer}>
                                <img src={rehome.photo} alt={rehome.name} style={styles.image} />
                            </div>
                        )}

                        <div style={styles.buttonContainer}>
                            <IconButton 
                                color="primary" 
                                onClick={() => handleEditRehomeClick(rehome)} 
                                disabled={rehome.status === 'ACCEPTED_REHOME'}
                            >
                                <EditIcon />
                            </IconButton>
                            <IconButton 
                                color="secondary" 
                                onClick={() => handleDeleteRehomeClick(rehome.pid)}
                            >
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
            {error && <Typography variant="body1" sx={{ color: 'red' }}>{error}</Typography>}
            <Grid container spacing={1}>
                <Typography variant="h6" sx={styles.centeredHeading}>Pending Rehome</Typography>
                <Grid container spacing={5}>
                    {renderRehomeCards(rehomes.filter((rehome) => rehome.status === 'PENDING_REHOME'))}
                </Grid>

                <Typography variant="h6" sx={styles.centeredHeading}>Approved Rehome</Typography>
                <Grid container spacing={1}>
                    {renderRehomeCards(rehomes.filter((rehome) => rehome.status === 'ACCEPTED_REHOME'))}
                </Grid>
            </Grid>

            {/* Edit Dialog */}
            <Dialog open={editDialogOpen} onClose={handleDialogClose}>
                <DialogTitle style={styles.textShade}>Edit Rehome Status</DialogTitle>
                <DialogContent>
                    <Select value={newStatus} onChange={handleStatusChange} fullWidth>
                        <MenuItem value="ACCEPTED_REHOME">Accepted</MenuItem>
                        <MenuItem value="REJECTED">Rejected</MenuItem>
                    </Select>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDialogClose} color="secondary">Cancel</Button>
                    <Button onClick={handleSaveRehomeStatus} color="primary">Save</Button>
                </DialogActions>
            </Dialog>

            {/* Confirmation Dialog */}
            <Dialog open={confirmDialogOpen} onClose={handleDialogClose}>
                <DialogTitle style={styles.textShade}>Confirm Deletion</DialogTitle>
                <DialogContent>
                    <Typography style={styles.textShade}>Are you sure you want to delete this rehome record?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDialogClose} color="secondary">Cancel</Button>
                    <Button onClick={handleDeleteRehome} color="primary">Delete</Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={!!successMessage}
                autoHideDuration={6000}
                onClose={() => setSuccessMessage('')}
                message={successMessage}
            />
        </div>
    );
};

const styles = {
        card: {
            width: '300px', 
            height: '640px', 
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            alignItems: 'center',
            margin: 'auto',
            marginBottom: '20px',
            padding: '10px',
            boxSizing: 'border-box',
        },
        buttonContainer: {
            marginTop: '10px',
            display: 'flex',
            justifyContent: 'space-between',
            width: '100%',
        },
        imageContainer: {
            height: '200px', 
            width: '100%',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            overflow: 'hidden', 
        },
        image: {
            width: '100%',
            height: '100%',
            objectFit: 'cover', 
        },
        textShade: {
            color: '#424242',
        },
        centeredHeading: {
            textAlign: 'center',
            fontFamily: "'Arial', sans-serif",
            fontWeight: 'bold',
            color: '#5A20A8',
            marginBottom: '20px',
            marginTop: '40px',
    },
};

export default PetDashboard;