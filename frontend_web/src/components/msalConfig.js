// filepath: c:\Projects\IT342-Pawlly\frontend_web\src\components\msalConfig.js
import { PublicClientApplication } from "@azure/msal-browser";

const msalConfig = {
    auth: {
        clientId: "f8b9184f-ed50-4b6c-87aa-381c5d7a947b", // Replace with your Microsoft App Client ID
        authority: "https://login.microsoftonline.com/823cde44-4433-456d-b801-bdf0ab3d41fc",
        redirectUri: `${process.env.REACT_APP_BACKEND_URL}/auth/microsoft/callback`, // Replace with your redirect URI
    },
};

export const msalInstance = new PublicClientApplication(msalConfig);

// Initialize the instance
msalInstance.initialize().catch((error) => {
    console.error("Error initializing MSAL:", error);
});