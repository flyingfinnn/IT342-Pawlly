import { useEffect } from "react";
import { msalInstance } from "./msalConfig";
import { useNavigate } from "react-router-dom";  // to handle navigation after login

const MicrosoftCallback = () => {
    const navigate = useNavigate(); // React Router's hook to navigate

    useEffect(() => {
        msalInstance.handleRedirectPromise()
            .then((response) => {
                if (response) {
                    console.log("Microsoft login successful:", response);
                    
                    // Assuming you want to store the access token or idToken for further use
                    const account = response.account;
                    const token = response.accessToken; // Or response.idToken, depending on what you need
                    localStorage.setItem('user', JSON.stringify(account)); // Store the user account info
                    localStorage.setItem('token', token); // Store the token for future API requests

                    // Optionally, redirect the user to the home page or dashboard after successful login
                    navigate('/dashboard'); // Example redirection
                }
            })
            .catch((error) => {
                console.error("Error handling Microsoft redirect:", error);
            });
    }, [navigate]); // Re-run when the navigate function changes

    return <div>Processing Microsoft login...</div>;
};

export default MicrosoftCallback;
