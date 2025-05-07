import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Box, Tabs, Tab, Typography, Snackbar, IconButton, Table,
  TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
  Tooltip, Dialog, DialogTitle, DialogContent, DialogActions, Select,
  MenuItem, Button
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const PetDashboard = ({ onPetAdded = () => {} }) => {
  const [rehomes, setRehomes] = useState([]);
  const [tab, setTab] = useState(0);
  const [editRehome, setEditRehome] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [error, setError] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [deleteRehomeId, setDeleteRehomeId] = useState(null);

  useEffect(() => {
    const fetchRehomes = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/pet/getAllPets`);
        setRehomes(response.data);
      } catch (error) {
        setError('Failed to load rehome records.');
      }
    };
    fetchRehomes();
  }, []);

  const handleTabChange = (_, newValue) => setTab(newValue);

  const handleEditRehomeClick = (rehome) => {
    setEditRehome(rehome);
    setNewStatus(rehome.status);
    setEditDialogOpen(true);
  };

  const handleDeleteRehomeClick = (id) => {
    setDeleteRehomeId(id);
    setConfirmDialogOpen(true);
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
        const updatedPet = { ...editRehome, status: newStatus };
        if (newStatus === 'REJECTED') {
          await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/pet/deletePetDetails/${editRehome.pid}`);
          setRehomes((prev) => prev.filter((r) => r.pid !== editRehome.pid));
          setSuccessMessage('Rehome record rejected and deleted.');
        } else {
          await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/pet/putPetDetails`, updatedPet, {
            params: { pid: editRehome.pid },
          });
          setRehomes((prev) =>
            prev.map((r) => (r.pid === editRehome.pid ? { ...r, status: newStatus } : r))
          );
          setSuccessMessage('Rehome updated.');
          onPetAdded(updatedPet);
        }
      }
    } catch (error) {
      setError('Failed to update rehome status.');
    } finally {
      handleDialogClose();
    }
  };

  const handleDeleteRehome = async () => {
    try {
      await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/pet/deletePetDetails/${deleteRehomeId}`);
      setRehomes((prev) => prev.filter((r) => r.pid !== deleteRehomeId));
      setSuccessMessage('Rehome record deleted.');
    } catch (error) {
      setError('Failed to delete rehome record.');
    } finally {
      handleDialogClose();
    }
  };

  const filteredRehomes = rehomes.filter((r) =>
    tab === 0 ? r.status === 'PENDING_REHOME' : r.status === 'ACCEPTED_REHOME'
  );

  const getStatusLabel = () => (tab === 0 ? 'Pending' : 'Accepted');

  return (
    <Box p={4}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" fontWeight="bold">{getStatusLabel()} Rehomes</Typography>
        <Tabs value={tab} onChange={handleTabChange}>
          <Tab label="Pending" />
          <Tab label="Accepted" />
        </Tabs>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell><strong>ID</strong></TableCell>
              <TableCell><strong>Name</strong></TableCell>
              <TableCell><strong>Type</strong></TableCell>
              <TableCell><strong>Breed</strong></TableCell>
              <TableCell><strong>Age</strong></TableCell>
              <TableCell><strong>Gender</strong></TableCell>
              <TableCell><strong>Description</strong></TableCell>
              <TableCell><strong>User Name</strong></TableCell>
              <TableCell><strong>Address</strong></TableCell>
              <TableCell><strong>Contact</strong></TableCell>
              <TableCell><strong>Submission Date</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell><strong>Photo</strong></TableCell>
              <TableCell align="center"><strong>Actions</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredRehomes.map((rehome) => (
              <TableRow key={rehome.pid}>
                <TableCell>{rehome.pid}</TableCell>
                <TableCell>{rehome.name}</TableCell>
                <TableCell>{rehome.type}</TableCell>
                <TableCell>{rehome.breed}</TableCell>
                <TableCell>{rehome.age}</TableCell>
                <TableCell>{rehome.gender}</TableCell>
                <TableCell>{rehome.description}</TableCell>
                <TableCell>{rehome.userName}</TableCell>
                <TableCell>{rehome.address}</TableCell>
                <TableCell>{rehome.contactNumber}</TableCell>
                <TableCell>{rehome.submissionDate || 'N/A'}</TableCell>
                <TableCell>{rehome.status}</TableCell>
                <TableCell>
                  {rehome.photo && (
                    <img src={rehome.photo} alt="pet" style={{ width: '60px', height: '60px', objectFit: 'cover' }} />
                  )}
                </TableCell>
                <TableCell align="center">
                  {rehome.status === 'PENDING_REHOME' && (
                    <>
                      <Tooltip title="Edit">
                        <IconButton color="primary" onClick={() => handleEditRehomeClick(rehome)}>
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Delete">
                        <IconButton color="error" onClick={() => handleDeleteRehomeClick(rehome.pid)}>
                          <DeleteIcon />
                        </IconButton>
                      </Tooltip>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Edit Rehome Status</DialogTitle>
        <DialogContent>
          <Select value={newStatus} onChange={(e) => setNewStatus(e.target.value)} fullWidth>
            <MenuItem value="ACCEPTED_REHOME">Accepted</MenuItem>
            <MenuItem value="REJECTED">Rejected</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary">Cancel</Button>
          <Button onClick={handleSaveRehomeStatus} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      {/* Confirm Delete Dialog */}
      <Dialog open={confirmDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this rehome record?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary">Cancel</Button>
          <Button onClick={handleDeleteRehome} color="error">Delete</Button>
        </DialogActions>
      </Dialog>

      {/* Feedback Snackbar */}
      <Snackbar
        open={!!successMessage}
        autoHideDuration={4000}
        onClose={() => setSuccessMessage('')}
        message={successMessage}
      />
      {error && <Typography sx={{ color: 'red', mt: 2 }}>{error}</Typography>}
    </Box>
  );
};

export default PetDashboard;
