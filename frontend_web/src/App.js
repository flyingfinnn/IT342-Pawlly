import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Signup from "./components/Signup";
import Login from "./components/Login";
import UserDashboard from "./components/Dashboards/UserDashboard";
import Home from "./components/Home";
import LostAndFound from "./components/LostAndFound";
import Sponsor from "./components/Sponsor";
import MicrosoftCallback from "./components/MicrosoftCallback";


import VolunteerSignUp from "./components/Volunteer/VolunteerSignUpList";
import CreateOpportunity from "./components/Volunteer/CreateOpportunity";
import Volunteer from "./components/Volunteer/Volunteer";
import VolunteerDashboard from "./components/Volunteer/VolunteerDashboard";
import OpportunityDetail from "./components/Volunteer/OpportunityDetail";
import UpdateOpportunity from "./components/Volunteer/UpdateOpportunity";
import AboutUs from "./components/AboutUs";

import { createTheme, ThemeProvider } from "@mui/material/styles";
import NoMatch from "./components/NoMatch";
import Profile from "./components/Profile";
import Navbar from "./components/Navbar";
import PetList from "./components/PetRehome/PetList";
import AdminDashboard from "./components/Dashboards/AdminDashboard";
import { UserProvider } from "./components/UserContext";
import ArticleDashboard from "./components/Dashboards/ArticleDashboard";
import User from "./components/User";

import Unauthorized from "./components/Unauthorized";
import ProtectedRoute from "./components/ProtectedRoute";




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
  const user = (() => {
    try {
      const storedUser = JSON.parse(localStorage.getItem("user"));
      return storedUser && storedUser.role ? storedUser : null; // Ensure role exists
    } catch {
      return null; // Handle invalid JSON gracefully
    }
  })();

  return (
    <UserProvider>
      <ThemeProvider theme={theme}>
        <Router>
          <div
            style={{
              paddingTop: "120px", 
            }}
          ></div>
          <Navbar />
          <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/auth/microsoft/callback" element={<MicrosoftCallback />} />;
            <Route path="/users" element={<UserDashboard />} />

            <Route path="/profile" element={<Profile />} />
            <Route path="/" element={<Home />} /> {/* Default to login */}
            <Route path="/home" element={<Home />} />
            <Route path="/lost-and-found" element={<LostAndFound />} />
            <Route path="/article_dash" element={<ArticleDashboard />} />
            <Route path="/sponsor" element={<Sponsor />} />
            <Route path="/adopt" element={<PetList />} />
            <Route path="*" element={<NoMatch />} />

            <Route path="/about-us" element={<AboutUs/>} />

            <Route path="/volunteer" element={<Volunteer />} />
            <Route path="/admin/manage-opportunities" element={<VolunteerDashboard />}/>
            <Route path="/admin/manage-volunteers" element={<VolunteerSignUp />}/>
            <Route path="/opportunity/:id" element={<OpportunityDetail />} />
            <Route path="/update-opportunity/:id" element={<UpdateOpportunity />} />
            <Route path="/book" element={<CreateOpportunity />} />
            <Route path="/unauthorized" element={<Unauthorized />} />
            <Route
                path="/admin"
                element={
                  <ProtectedRoute user={user}>
                    <AdminDashboard />
                  </ProtectedRoute>
                }
            />
            <Route path="/user/:id" element={<User />} />
          </Routes>
        </Router>
      </ThemeProvider>
    </UserProvider>
  );
}

export default App;
