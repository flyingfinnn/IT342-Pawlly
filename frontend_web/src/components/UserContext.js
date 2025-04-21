import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUser = async () => {
            const token = localStorage.getItem('token');
            const refreshToken = localStorage.getItem('refreshToken');
            if (token) {
                try {
                    const response = await axios.get('http://localhost:8080/api/users/me', {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    });
                    setUser(response.data);
                } catch (error) {
                    if (error.response?.status === 401 && refreshToken) {
                        try {
                            const refreshResponse = await axios.post(
                                'http://localhost:8080/api/auth/refresh',
                                {},
                                { headers: { 'Refresh-Token': refreshToken } }
                            );
                            const newAccessToken = refreshResponse.headers['new-access-token'];
                            localStorage.setItem('token', newAccessToken);
                            fetchUser(); // Retry fetching user
                        } catch (refreshError) {
                            console.error('Refresh token failed:', refreshError);
                            clearUser();
                        }
                    } else {
                        console.error('Error fetching user data:', error);
                    }
                }

            }
            setLoading(false);
        };
        fetchUser();
    }, []);

    const updateUser = (newUser) => {
        setUser(newUser);
        localStorage.setItem('user', JSON.stringify(newUser));
    };

    const clearUser = () => {
        setUser(null);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    return (
        <UserContext.Provider value={{ user, updateUser, clearUser, loading }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => useContext(UserContext);
