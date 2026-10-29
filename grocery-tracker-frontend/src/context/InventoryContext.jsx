import { createContext, useContext, useEffect, useState } from "react";
import axios from "axios";

const InventoryContext = createContext();

export function InventoryProvider({ children }) {
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchInventory = async () => {
            try {
                const token = localStorage.getItem("token");

                const response = await axios.get("/api/inventory", {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setInventory(response.data);
            } catch (error) {
                console.error("Error fetching inventory:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchInventory();
    }, []);

    return (
        <InventoryContext.Provider
            value={{ inventory, setInventory, loading }}
        >
            {children}
        </InventoryContext.Provider>
    );
}

export function useInventory() {
    return useContext(InventoryContext);
}