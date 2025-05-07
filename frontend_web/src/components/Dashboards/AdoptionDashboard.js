import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Tabs,
  Tab,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  IconButton,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Select,
  MenuItem,
  Button,
  TextField,
  InputAdornment,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import SearchIcon from '@mui/icons-material/Search';

const AdoptionDashboard = () => {
  const [adoptions, setAdoptions] = useState([]);
  const [tab, setTab] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [editAdoption, setEditAdoption] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAdoptions = async () => {
      try {
        const res = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`);
        setAdoptions(res.data);
      } catch (err) {
        setError('Failed to load adoptions.');
      }
    };
    fetchAdoptions();
  }, []);

  const handleEditClick = (adoption) => {
    setEditAdoption(adoption);
    setNewStatus(adoption.status);
    setEditDialogOpen(true);
  };

  const handleDeleteClick = (id) => {
    setDeleteId(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${deleteId}`);
      setAdoptions((prev) => prev.filter((a) => a.adoptionID !== deleteId));
      setSuccessMessage('Deleted successfully!');
    } catch {
      setError('Failed to delete.');
    } finally {
      setDeleteDialogOpen(false);
    }
  };

  const handleSaveAdoption = async () => {
    try {
      if (editAdoption) {
        const updated = { ...editAdoption, status: newStatus };
        await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${editAdoption.adoptionID}`, updated);
        setAdoptions((prev) =>
          prev.map((a) => (a.adoptionID === editAdoption.adoptionID ? { ...a, ...updated } : a))
        );
        setSuccessMessage('Status updated!');
        setEditDialogOpen(false);
      }
    } catch {
      setError('Failed to update.');
    }
  };

  const filtered = adoptions.filter(
    (a) =>
      a.status === ['PENDING', 'APPROVED', 'REJECTED'][tab] &&
      (a.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        a.address?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        a.breed?.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <Box p={3}>
      <Typography variant="h5" gutterBottom color="primary">Adoption Dashboard</Typography>

      <Tabs value={tab} onChange={(e, v) => setTab(v)} textColor="primary" indicatorColor="primary" sx={{ mb: 2 }}>
        <Tab label="Pending" />
        <Tab label="Approved" />
        <Tab label="Rejected" />
      </Tabs>

      <TextField
        variant="outlined"
        placeholder="Search by name, breed, or address..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        fullWidth
        sx={{ mb: 3 }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon color="action" />
            </InputAdornment>
          ),
        }}
      />

      {error && <Typography color="error">{error}</Typography>}

      <Grid container spacing={3}>
        {filtered.map((a) => (
          <Grid item xs={12} sm={6} md={4} key={a.adoptionID}>
            <Card sx={{ height: 580, display: 'flex', flexDirection: 'column', p: 2 }}>
              {a.photo && (
                <Box sx={{ height: 180, overflow: 'hidden', mb: 2 }}>
                  <img src={a.photo} alt="Pet" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                </Box>
              )}
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography variant="subtitle1"><strong>{a.name}</strong> ({a.petType})</Typography>
                <Typography variant="body2">Breed: {a.breed}</Typography>
                <Typography variant="body2">Address: {a.address}</Typography>
                <Typography variant="body2">Contact: {a.contactNumber}</Typography>
                <Typography variant="body2">Date: {a.adoptionDate}</Typography>
                <Typography variant="body2" gutterBottom>Description: {a.description}</Typography>
                <Typography variant="body2"><strong>Status: {a.status}</strong></Typography>
              </CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                <IconButton onClick={() => handleEditClick(a)} disabled={a.status !== 'PENDING'}>
                  <EditIcon />
                </IconButton>
                <IconButton color="error" onClick={() => handleDeleteClick(a.adoptionID)}>
                  <DeleteIcon />
                </IconButton>
              </Box>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)}>
        <DialogTitle>Edit Status</DialogTitle>
        <DialogContent>
          <Select value={newStatus} onChange={(e) => setNewStatus(e.target.value)} fullWidth>
            <MenuItem value="APPROVED">Approved</MenuItem>
            <MenuItem value="REJECTED">Rejected</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleSaveAdoption}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Delete Confirmation</DialogTitle>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button color="error" onClick={handleDeleteConfirm}>Delete</Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={!!successMessage}
        autoHideDuration={4000}
        onClose={() => setSuccessMessage('')}
        message={successMessage}
      />
    </Box>
  );
};

export default AdoptionDashboard;
