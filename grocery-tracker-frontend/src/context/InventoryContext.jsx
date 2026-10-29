import { createContext, useCallback, useContext, useEffect, useState } from "react";
import apiClient from "../utils/apiClient";

const InventoryContext = createContext();

export function InventoryProvider({ children }) {
    const [inventory, setInventory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const refreshInventory = useCallback(async () => {
        if (!localStorage.getItem("token")) {
            setInventory([]);
            setError("");
            setLoading(false);
            return;
        }

        setLoading(true);

        try {
            const response = await apiClient.get("/api/inventory");
            setInventory(response.data);
            setError("");
        } catch (fetchError) {
            console.error("Error fetching inventory:", fetchError);
            setError("Unable to load your inventory. Please try again.");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void refreshInventory();
        window.addEventListener("authentication-changed", refreshInventory);

        return () => {
            window.removeEventListener("authentication-changed", refreshInventory);
        };
    }, [refreshInventory]);

    return (
        <InventoryContext.Provider
            value={{ inventory, setInventory, loading, error, refreshInventory }}
        >
            {children}
        </InventoryContext.Provider>
    );
}

// Context hooks are intentionally colocated with their provider.
// eslint-disable-next-line react-refresh/only-export-components
export function useInventory() {
    return useContext(InventoryContext);
}
