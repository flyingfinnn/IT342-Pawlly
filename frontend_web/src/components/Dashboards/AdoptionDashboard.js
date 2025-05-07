import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  Box,
  Tabs,
  Tab,
  Typography,
  Snackbar,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Tooltip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const AdoptionDashboard = () => {
  const [adoptions, setAdoptions] = useState([]);
  const [tab, setTab] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editAdoptionData, setEditAdoptionData] = useState(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteAdoptionId, setDeleteAdoptionId] = useState(null);

  useEffect(() => {
    const fetchAdoptions = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`);
        setAdoptions(response.data);
      } catch (err) {
        console.error('Failed to load records.');
      }
    };
    fetchAdoptions();
  }, []);

  const handleTabChange = (_, newValue) => {
    setTab(newValue);
  };

  const updateStatus = async (adoptionID, newStatus) => {
    try {
      await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${adoptionID}`, { status: newStatus });
      setAdoptions((prev) => prev.map((a) => (a.adoptionID === adoptionID ? { ...a, status: newStatus } : a)));
      setSuccessMessage(`Adoption ${newStatus.toLowerCase()} successfully.`);
    } catch (err) {
      console.error('Failed to update status');
    }
  };

  const handleEditDialogClose = () => {
    setEditDialogOpen(false);
    setEditAdoptionData(null);
  };

  const handleEditInputChange = (e) => {
    const { name, value } = e.target;
    setEditAdoptionData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSaveEdit = async () => {
    try {
      await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${editAdoptionData.adoptionID}`, editAdoptionData);
      setSuccessMessage('Adoption record updated successfully!');
      setEditDialogOpen(false);
      setAdoptions((prev) =>
        prev.map((adoption) =>
          adoption.adoptionID === editAdoptionData.adoptionID ? editAdoptionData : adoption
        )
      );
    } catch (err) {
      console.error('Failed to update adoption record.');
    }
  };

  const handleDeleteClick = (adoptionID) => {
    setDeleteAdoptionId(adoptionID);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${deleteAdoptionId}`);
      setAdoptions((prev) => prev.filter((a) => a.adoptionID !== deleteAdoptionId));
      setDeleteDialogOpen(false);
      setSuccessMessage('Adoption record deleted successfully!');
    } catch (err) {
      console.error('Failed to delete adoption record.');
    }
  };

  const filteredAdoptions = adoptions.filter((a) => {
    if (tab === 0) return a.status === 'PENDING';
    if (tab === 1) return a.status === 'APPROVED';
    if (tab === 2) return a.status === 'REJECTED';
    return false;
  });

  const getStatusLabel = () => ['Pending', 'Approved', 'Rejected'][tab];

  return (
    <Box p={4}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" fontWeight="bold">{getStatusLabel()} Adoptions</Typography>
        <Tabs value={tab} onChange={handleTabChange}>
          <Tab label="Pending" />
          <Tab label="Approved" />
          <Tab label="Rejected" />
        </Tabs>
      </Box>

      <TableContainer component={Paper} elevation={3}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell><strong>ID</strong></TableCell>
              <TableCell><strong>Name</strong></TableCell>
              <TableCell><strong>Pet</strong></TableCell>
              <TableCell><strong>Breed</strong></TableCell>
              <TableCell><strong>Contact</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell align="center"><strong>Actions</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredAdoptions.map((adoption) => (
              <TableRow key={adoption.adoptionID}>
                <TableCell>{adoption.adoptionID}</TableCell>
                <TableCell>{adoption.name}</TableCell>
                <TableCell>{adoption.petType}</TableCell>
                <TableCell>{adoption.breed}</TableCell>
                <TableCell>{adoption.contactNumber}</TableCell>
                <TableCell>{adoption.status}</TableCell>
                <TableCell align="center">
                  {adoption.status === 'PENDING' && (
                    <>
                      <Tooltip title="Approve">
                        <IconButton color="success" onClick={() => updateStatus(adoption.adoptionID, 'APPROVED')}>
                          <CheckCircleIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Reject">
                        <IconButton color="error" onClick={() => updateStatus(adoption.adoptionID, 'REJECTED')}>
                          <CancelIcon />
                        </IconButton>
                      </Tooltip>
                    </>
                  )}
                  <Tooltip title="Edit">
                    <IconButton color="primary" onClick={() => { setEditAdoptionData(adoption); setEditDialogOpen(true); }}>
                      <EditIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Delete">
                    <IconButton color="secondary" onClick={() => handleDeleteClick(adoption.adoptionID)}>
                      <DeleteIcon />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Snackbar
        open={!!successMessage}
        autoHideDuration={4000}
        onClose={() => setSuccessMessage('')}
        message={successMessage}
      />

      {/* Edit Adoption Dialog */}
      <Dialog open={editDialogOpen} onClose={handleEditDialogClose}>
        <DialogTitle>Edit Adoption</DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            fullWidth
            margin="normal"
            name="name"
            value={editAdoptionData?.name || ''}
            onChange={handleEditInputChange}
          />
          <TextField
            label="Contact Number"
            fullWidth
            margin="normal"
            name="contactNumber"
            value={editAdoptionData?.contactNumber || ''}
            onChange={handleEditInputChange}
          />
          <TextField
            label="Pet Type"
            fullWidth
            margin="normal"
            name="petType"
            value={editAdoptionData?.petType || ''}
            onChange={handleEditInputChange}
          />
          <TextField
            label="Breed"
            fullWidth
            margin="normal"
            name="breed"
            value={editAdoptionData?.breed || ''}
            onChange={handleEditInputChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditDialogClose} color="primary">Cancel</Button>
          <Button onClick={handleSaveEdit} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Adoption Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} color="primary">Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="secondary">Delete</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default AdoptionDashboard;
