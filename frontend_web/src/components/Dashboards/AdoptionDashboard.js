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
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';

const AdoptionDashboard = () => {
  const [adoptions, setAdoptions] = useState([]);
  const [tab, setTab] = useState(0);
  const [successMessage, setSuccessMessage] = useState('');

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
      await axios.put(`${process.env.REACT_APP_BACKEND_URL}/api/adoptions/${adoptionID}`, {
        status: newStatus,
      });
      setAdoptions((prev) =>
        prev.map((a) => (a.adoptionID === adoptionID ? { ...a, status: newStatus } : a))
      );
      setSuccessMessage(`Adoption ${newStatus.toLowerCase()} successfully.`);
    } catch (err) {
      console.error('Failed to update status');
    }
  };

  const filteredAdoptions = adoptions.filter((a) => {
    if (tab === 0) return a.status === 'PENDING';
    if (tab === 1) return a.status === 'APPROVED';
    if (tab === 2) return a.status === 'REJECTED';
    return false;
  });

  const getStatusLabel = () => ['Pending', 'Accepted', 'Rejected'][tab];

  return (
    <Box p={4}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" fontWeight="bold">{getStatusLabel()} Adoptions</Typography>
        <Tabs value={tab} onChange={handleTabChange}>
          <Tabs
            value={tab}
            onChange={handleTabChange}
            TabIndicatorProps={{
              sx: {
                backgroundColor:
                  tab === 0 ? 'gold' : tab === 1 ? 'green' : 'red', // Indicator underline color
              },
            }}
            textColor="inherit"
          >
            <Tab
              label="Pending"
              sx={{
                color: tab === 0 ? 'gold' : 'gray',
                fontWeight: tab === 0 ? 'bold' : 'normal',
              }}
            />
            <Tab
              label="Accepted"
              sx={{
                color: tab === 1 ? 'green' : 'gray',
                fontWeight: tab === 1 ? 'bold' : 'normal',
              }}
            />
            <Tab
              label="Rejected"
              sx={{
                color: tab === 2 ? 'red' : 'gray',
                fontWeight: tab === 2 ? 'bold' : 'normal',
              }}
            />
          </Tabs>
        </Tabs>
      </Box>

      <TableContainer component={Paper} elevation={3}>
        <Table>
          <TableHead>
            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
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
    </Box>
  );
};

export default AdoptionDashboard;
