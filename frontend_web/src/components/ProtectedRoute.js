import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ user, children }) => {
    // Log user and roles to debug
    console.log('User:', user);
    console.log('User Roles:', user ? user.role : 'No user data');

    // Check if user or user.roles is undefined
    if (!user || !user.role) {
        console.log('Redirecting to homepage due to missing user or roles');
        return <Navigate to="/" />;
    }

    // Check if the user has the correct role (example: 'ROLE_ADMIN')
    if (user.role.includes('ROLE_ADMIN')) {
        console.log('User has the required role, rendering protected route');
        return children; // Render the protected route if the user has the required role
    }

    // Redirect to unauthorized page if the user does not have the required role
    console.log('User does not have the required role, redirecting to unauthorized page');
    return <Navigate to="/unauthorized" />;
};

export default ProtectedRoute;
