import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Signup from "./components/Signup";
import Login from "./components/Login";
import UserDashboard from "./components/Dashboards/UserDashboard";
import Home from "./components/Home";
import Sponsor from "./components/Sponsor";
import AdoptionForm from "./components/AdoptionForm";
import AdoptionList from "./components/AdoptionList";


import VolunteerSignUp from "./components/Volunteer/VolunteerSignUpList";
import CreateOpportunity from "./components/Volunteer/CreateOpportunity";
import Volunteer from "./components/Volunteer/Volunteer";
import VolunteerOpportunities from "./components/Volunteer/VolunteerOpportunities";
import OpportunityDetail from "./components/Volunteer/OpportunityDetail";
import UpdateOpportunity from "./components/Volunteer/UpdateOpportunity"

import { createTheme, ThemeProvider } from "@mui/material/styles";
import NoMatch from "./components/NoMatch";
import Profile from "./components/Profile";
import Navbar from "./components/Navbar";
import PetList from "./components/PetList";
import AdminDashboard from "./components/Dashboards/AdminDashboard";
import { UserProvider } from "./components/UserContext";
import User from "./components/User";


const theme = createTheme({
  palette: {
    primary: {
      main: "#675BC8",
    },
    mode: "light",
  },
  components: {
    MuiInputAdornment: {
      styleOverrides: {
        root: {
          color: "#675BC8",
        },
      },
    },
  },
});


function App() {
  return (
    <UserProvider>
      <ThemeProvider theme={theme}>
        <Router>
          <div
            style={{
              paddingTop: "120px", // Adjust based on your Navbar height
            }}
          ></div>
          <Navbar />
          <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/users" element={<UserDashboard />} />

            <Route path="/profile" element={<Profile />} />
            <Route path="/" element={<Home />} /> {/* Default to login */}
            <Route path="/home" element={<Home />} />
            <Route path="/sponsor" element={<Sponsor />} />
            <Route path="/adopt" element={<PetList />} />
            <Route path="*" element={<NoMatch />} />
            <Route path="/adopt_dash" element={<AdoptionList />} />
            <Route path="/volunteer" element={<Volunteer />} />
            <Route path="/admin/manage-opportunities" element={<VolunteerOpportunities />}/>
            <Route path="/admin/manage-volunteers" element={<VolunteerSignUp />}/>
            <Route path="/admin/adoption-list" element={<AdoptionList />} />
            <Route path="/opportunity/:id" element={<OpportunityDetail />} />
            <Route path="/update-opportunity/:id" element={<UpdateOpportunity />} />
            <Route path="/book" element={<CreateOpportunity />} />
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/user/:id" element={<User />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </UserProvider>
  );
}

export default App;
