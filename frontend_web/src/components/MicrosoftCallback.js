// filepath: c:\Projects\IT342-Pawlly\frontend_web\src\components\MicrosoftCallback.js
import { useEffect } from "react";
import { msalInstance } from "./msalConfig";

const MicrosoftCallback = () => {
    useEffect(() => {
        msalInstance.handleRedirectPromise()
            .then((response) => {
                if (response) {
                    console.log("Microsoft login successful:", response);
                    // Handle the response (e.g., send token to backend)
                }
            })
            .catch((error) => {
                console.error("Error handling Microsoft redirect:", error);
            });
    }, []);

    return <div>Processing Microsoft login...</div>;
};

export default MicrosoftCallback;