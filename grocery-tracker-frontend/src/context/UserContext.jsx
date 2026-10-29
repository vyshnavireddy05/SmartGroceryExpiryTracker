import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";

const UserContext = createContext();

export function UserProvider({ children }) {

    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const fetchUserDetails = async () => {

            try {

                const token = localStorage.getItem("token");

                if (!token) {
                    setProfile(null);
                    return;
                }

                const response = await axios.get(
                    "/api/settings/Details",
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                setProfile(response.data);

            } catch (error) {

                console.error(
                    "Error fetching user details:",
                    error
                );

            } finally {

                setLoading(false);

            }
        };

        fetchUserDetails();

    }, []);

    return (
        <UserContext.Provider
            value={{
                profile,
                setProfile,
                loading
            }}
        >
            {children}
        </UserContext.Provider>
    );
}

// Context hooks are intentionally colocated with their provider.
// eslint-disable-next-line react-refresh/only-export-components
export function useUser() {
    return useContext(UserContext);
}