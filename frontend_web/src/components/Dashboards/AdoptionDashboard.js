import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  Box, Tabs, Tab, Typography, Snackbar, IconButton, Table,
  TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
  Tooltip, Dialog, DialogTitle, DialogContent, DialogActions, Select,
  MenuItem, Button, TextField, TablePagination
} from '@mui/material';
import CheckIcon from '@mui/icons-material/Check';
import ClearIcon from '@mui/icons-material/Clear';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const AdoptionDashboard = () => {
  const [adoptions, setAdoptions] = useState([]);
  const [tab, setTab] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [editAdoption, setEditAdoption] = useState(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [newStatus, setNewStatus] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  useEffect(() => {
    const fetchAdoptions = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions`);
        setAdoptions(response.data);
      } catch (error) {
        setError('Failed to load adoption records.');
      }
    };
    fetchAdoptions();
  }, []);

  const handleTabChange = (_, newValue) => {
    setTab(newValue);
    setPage(0);
  };

  const handleEditClick = (adoption) => {
    setEditAdoption(adoption);
    setNewStatus(adoption.status);
    setEditDialogOpen(true);
  };

  const handleDeleteClick = (adoptionId) => {
    setDeleteId(adoptionId);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${deleteId}`);
      setAdoptions(adoptions.filter((a) => a.adoptionID !== deleteId));
      setSuccessMessage('Adoption record deleted successfully!');
    } catch {
      setError('Failed to delete adoption record.');
    } finally {
      setDeleteDialogOpen(false);
    }
  };

  const handleDialogClose = () => {
    setEditDialogOpen(false);
    setEditAdoption(null);
  };

  const handleSaveAdoption = async () => {
    try {
      const updatedAdoption = { ...editAdoption, status: newStatus };
      await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${editAdoption.adoptionID}`, updatedAdoption);
      setAdoptions((prev) => prev.map((a) => a.adoptionID === editAdoption.adoptionID ? updatedAdoption : a));
      setSuccessMessage('Adoption record updated successfully!');
    } catch {
      setError('Failed to update adoption record.');
    } finally {
      handleDialogClose();
    }
  };

  const filteredAdoptions = adoptions.filter((a) => {
    const statusMap = ['PENDING', 'APPROVED', 'REJECTED'];
    return a.status === statusMap[tab] &&
      (a.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
       a.breed?.toLowerCase().includes(searchQuery.toLowerCase()));
  });

  const paginatedAdoptions = filteredAdoptions.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  return (
    <Box p={4}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" fontWeight="bold">Adoption Records</Typography>
        <Tabs value={tab} onChange={handleTabChange}>
          <Tab label="Pending" />
          <Tab label="Approved" />
          <Tab label="Rejected" />
        </Tabs>
      </Box>

      <TextField
        fullWidth
        label="Search by name or breed"
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        sx={{ mb: 2 }}
      />

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
              <TableCell><strong>ID</strong></TableCell>
              <TableCell><strong>Name</strong></TableCell>
              <TableCell><strong>Address</strong></TableCell>
              <TableCell><strong>Contact</strong></TableCell>
              <TableCell><strong>Pet Type</strong></TableCell>
              <TableCell><strong>Breed</strong></TableCell>
              <TableCell><strong>Description</strong></TableCell>
              <TableCell><strong>Submission Date</strong></TableCell>
              <TableCell><strong>Status</strong></TableCell>
              <TableCell><strong>Photo</strong></TableCell>
              <TableCell align="center"><strong>Actions</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {paginatedAdoptions.map((a) => (
              <TableRow key={a.adoptionID}>
                <TableCell>{a.adoptionID}</TableCell>
                <TableCell>{a.name}</TableCell>
                <TableCell>{a.address}</TableCell>
                <TableCell>{a.contactNumber}</TableCell>
                <TableCell>{a.petType}</TableCell>
                <TableCell>{a.breed}</TableCell>
                <TableCell>{a.description}</TableCell>
                <TableCell>{a.adoptionDate}</TableCell>
                <TableCell>{a.status}</TableCell>
                <TableCell>
                  {a.photo && (
                    <img src={a.photo} alt="pet" style={{ width: '60px', height: '60px', objectFit: 'cover' }} />
                  )}
                </TableCell>
                <TableCell align="center">
                  {a.status === 'PENDING' && (
                    <Tooltip title="Approve">
                      <IconButton color="success" onClick={() => handleEditClick(a)}>
                        <CheckIcon />
                      </IconButton>
                    </Tooltip>
                  )}
                  <Tooltip title="Edit">
                    <IconButton color="primary" onClick={() => handleEditClick(a)}>
                      <EditIcon />
                    </IconButton>
                  </Tooltip>
                  <Tooltip title="Delete">
                    <IconButton color="error" onClick={() => handleDeleteClick(a.adoptionID)}>
                      <DeleteIcon />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <TablePagination
          component="div"
          count={filteredAdoptions.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => {
            setRowsPerPage(parseInt(e.target.value, 10));
            setPage(0);
          }}
        />
      </TableContainer>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Edit Adoption Status</DialogTitle>
        <DialogContent>
          <Select label="Status" value={newStatus} onChange={(e) => setNewStatus(e.target.value)} fullWidth>
            <MenuItem value="APPROVED">Approved</MenuItem>
            <MenuItem value="REJECTED">Rejected</MenuItem>
          </Select>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="secondary">Cancel</Button>
          <Button onClick={handleSaveAdoption} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Are you sure you want to delete this adoption?</DialogTitle>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} color="secondary">Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="primary">Delete</Button>
        </DialogActions>
      </Dialog>

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

export default AdoptionDashboard;
