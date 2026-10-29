import { useMemo, useState } from "react";
import styles from "../styling/InventoryOverview.module.css";
import axios from "axios";
import { useInventory } from "../context/InventoryContext";
export default function InventoryOverview({ filter = "all" }) {
    const [search, setSearch] = useState("");
    const [category, setCategory] = useState("all");
    const { inventory, setInventory } = useInventory();
    // Increase quantity
    const increaseQuantity = async (id, currentQuantity) => {

        try {

            const newQuantity = currentQuantity + 1;
            const token = localStorage.getItem("token");

            if (!token) {
                alert("Please log in to update your inventory.");
                return;
            }

            const response = await axios.put(
                `/api/inventory/${id}/quantity`,
                {
                    quantity: newQuantity,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );


            setInventory((prevInventory) =>
                prevInventory.map((item) =>
                    item.id === id
                        ? {
                            ...item,
                            quantity: response.data.quantity
                        }
                        : item
                )
            );

        } catch (error) {
            console.error("Error increasing quantity:", error);
        }
    };

    // Decrease quantity
    const decreaseQuantity = async (id, currentQuantity) => {
        try {
            const newQuantity = Math.max(0, currentQuantity - 1);

            const response = await axios.put(
                `/api/inventory/${id}/quantity`,
                {
                    quantity: newQuantity
                }
            );

            if (newQuantity === 0) {
                // Remove item from frontend state
                setInventory((prevInventory) =>
                    prevInventory.filter(item => item.id !== id)
                );
            } else {
                // Update quantity normally
                setInventory((prevInventory) =>
                    prevInventory.map(item =>
                        item.id === id
                            ? {
                                ...item,
                                quantity: response.data.quantity
                            }
                            : item
                    )
                );
            }

        } catch (error) {
            console.error("Error decreasing quantity:", error);
        }
    };

    // Filtered items based on search and category
    const filteredItems = useMemo(() => {

        return inventory.filter((item) => {

            const itemStatus = item.status?.toLowerCase();

            const matchesFilter =
                filter === "all" || itemStatus === filter;

            const matchesSearch =
                (item.itemName || item.name || "")
                    .toLowerCase()
                    .includes(search.toLowerCase());

            const matchesCategory =
                category === "all" ||
                item.category === category;

            return (
                matchesFilter &&
                matchesSearch &&
                matchesCategory
            );
        });

    }, [filter, search, category, inventory]);
    return (

        <div className={styles.inventoryOverview}>

            {/* Header */}
            <div className={styles.topSection}>

                <div>

                    <h2>
                        Inventory Overview
                    </h2>

                    <p>
                        {filter === "all"
                            ? "Manage your grocery inventory"
                            : `Showing ${filter} items`}
                    </p>

                </div>


                <div className={styles.controls}>

                    {/* Search */}
                    <div className={styles.searchBox}>

                        <span>🔍</span>

                        <input
                            type="text"
                            placeholder="Search items..."
                            value={search}
                            onChange={(e) =>
                                setSearch(e.target.value)
                            }
                        />

                    </div>


                    {/* Category */}
                    <select
                        value={category}
                        onChange={(e) =>
                            setCategory(e.target.value)
                        }
                    >

                        <option value="all">
                            All Categories
                        </option>

                        <option value="Dairy">
                            Dairy
                        </option>

                        <option value="Fruits">
                            Fruits
                        </option>

                        <option value="Bakery">
                            Bakery
                        </option>

                    </select>

                </div>

            </div>


            {/* Table */}
            <div className={styles.tableContainer}>

                <table>

                    <thead>

                        <tr>

                            <th>Item</th>

                            <th>Category</th>

                            <th>Expiry Date</th>

                            <th>Status</th>

                            <th>Quantity</th>

                        </tr>

                    </thead>


                    <tbody>

                        {filteredItems.length > 0 ? (

                            filteredItems.map((item) => (

                                <tr key={item.id}>

                                    {/* Item */}
                                    <td>

                                        <div className={styles.itemName}>

                                            {item.itemName ||
                                                item.name}

                                        </div>

                                    </td>


                                    {/* Category */}
                                    <td>
                                        {item.category}
                                    </td>


                                    {/* Expiry Date */}
                                    <td>
                                        {item.expiryDate}
                                    </td>


                                    {/* Status */}
                                    <td>
                                        <span
                                            className={`${styles.status} ${styles[item.status?.toLowerCase() || "fresh"]
                                                }`}
                                        >
                                            {item.status === "FRESH"
                                                ? "Fresh"
                                                : item.status === "EXPIRING"
                                                    ? "Expiring Soon"
                                                    : "Expired"
                                            }
                                        </span>
                                    </td>


                                    {/* Quantity */}
                                    <td>

                                        <div
                                            className={
                                                styles.quantityControl
                                            }
                                        >

                                            {/* Decrease */}
                                            <button
                                                type="button"
                                                className={
                                                    styles.quantityButton
                                                }
                                                onClick={() =>
                                                    decreaseQuantity(
                                                        item.id,
                                                        item.quantity
                                                    )
                                                }
                                                disabled={
                                                    item.quantity ===
                                                    0
                                                }
                                            >
                                                −
                                            </button>


                                            {/* Quantity */}
                                            <span
                                                className={
                                                    styles.quantityValue
                                                }
                                            >
                                                {item.quantity}
                                            </span>


                                            {/* Increase */}
                                            <button
                                                type="button"
                                                className={
                                                    styles.quantityButton
                                                }
                                                onClick={() =>
                                                    increaseQuantity(
                                                        item.id,
                                                        item.quantity
                                                    )
                                                }
                                            >
                                                +
                                            </button>

                                        </div>

                                    </td>

                                </tr>

                            ))

                        ) : (

                            <tr>

                                <td
                                    colSpan="5"
                                    className={styles.noItems}
                                >
                                    No items found
                                </td>

                            </tr>

                        )}

                    </tbody>

                </table>

            </div>

        </div>
    );
}