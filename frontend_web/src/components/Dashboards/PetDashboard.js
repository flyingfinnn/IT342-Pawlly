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
import DeleteIcon from '@mui/icons-material/Delete';


const PetDashboard = ({ onPetAdded = () => {} }) => {
  const [rehomes, setRehomes] = useState([]);
  const [tab, setTab] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);

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

  const handleTabChange = (_, newValue) => {
    setTab(newValue);
    setPage(0);
  };

  const handleUpdateStatus = async (rehome, status) => {
    try {
      const updatedPet = { ...rehome, status };
      await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/pet/putPetDetails`, updatedPet, {
        params: { pid: rehome.pid },
      });
      setRehomes((prev) =>
        prev.map((r) => (r.pid === rehome.pid ? { ...r, status } : r))
      );
      setSuccessMessage(status === 'ACCEPTED_REHOME' ? 'Rehome accepted.' : 'Rehome rejected.');
      onPetAdded(updatedPet);
    } catch (error) {
      setError('Failed to update rehome status.');
    }
  };

  const handleDeletePet = async (pid) => {
    try {
      await axios.delete(`${process.env.REACT_APP_BACKEND_URL}/api/pet/deletePetDetails/${pid}`);
      setRehomes((prev) => prev.filter((r) => r.pid !== pid));
      setSuccessMessage('Pet rehome record deleted.');
    } catch (error) {
      setError('Failed to delete rehome record.');
    }
  };



  const filteredRehomes = rehomes.filter((r) => {
    const statusMap = ['PENDING_REHOME', 'ACCEPTED_REHOME', 'REJECTED'];
    return r.status === statusMap[tab] &&
           (r.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
            r.breed?.toLowerCase().includes(searchQuery.toLowerCase()));
  });

  const paginatedRehomes = filteredRehomes.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  return (
    <Box p={4}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" fontWeight="bold">Pet Rehomes</Typography>
        <Tabs value={tab} onChange={handleTabChange}>
          <Tab label="Pending" />
          <Tab label="Accepted" />
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
            {paginatedRehomes.map((rehome) => (
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
                      <Tooltip title="Accept">
                        <IconButton color="success" onClick={() => handleUpdateStatus(rehome, 'ACCEPTED_REHOME')}>
                          <CheckIcon />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Reject">
                        <IconButton color="error" onClick={() => handleUpdateStatus(rehome, 'REJECTED')}>
                          <ClearIcon />
                        </IconButton>
                      </Tooltip>
                      <TableCell align="center">
                        {rehome.status === 'PENDING_REHOME' && (
                          <>
                            <Tooltip title="Accept">
                              <IconButton color="success" onClick={() => handleUpdateStatus(rehome, 'ACCEPTED_REHOME')}>
                                <CheckIcon />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Reject">
                              <IconButton color="error" onClick={() => handleUpdateStatus(rehome, 'REJECTED')}>
                                <ClearIcon />
                              </IconButton>
                            </Tooltip>
                          </>
                        )}
                        <Tooltip title="Delete">
                          <IconButton onClick={() => handleDeletePet(rehome.pid)}>
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

        <TablePagination
          component="div"
          count={filteredRehomes.length}
          page={page}
          onPageChange={(e, newPage) => setPage(newPage)}
          rowsPerPage={rowsPerPage}
          onRowsPerPageChange={(e) => {
            setRowsPerPage(parseInt(e.target.value, 10));
            setPage(0);
          }}
        />
      </TableContainer>

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