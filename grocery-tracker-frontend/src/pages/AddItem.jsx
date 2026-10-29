import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "../styling/AddItemForm.module.css";
import axios from "axios";
export default function AddItemForm() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        itemName: "",
        category: "",
        price: "",
        quantity: "",
        unit:"",
        purchasedDate: "",
        expiryDate: "",
        storageLocation: "",
        notes: "",
    });

    const addInventory = async () => {
        try {
            const response = await axios.post('/api/inventory/addItem', formData);
            console.log("Item added successfully:", response.data);
            alert("Item added successfully!");
            navigate("/dashboard");
        } catch (error) {
            console.error("Error adding item:", error);
        }
    }

    function handleChange(e) {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    }

    function handleSubmit(e) {
        e.preventDefault();
        addInventory();
    }

    return (
        <div className={styles.page}>

            <div className={styles.formContainer}>

                <div className={styles.formHeader}>
                    <h1>Add Grocery Item</h1>

                    <p>
                        Add a new grocery item to your inventory
                    </p>
                </div>


                <form onSubmit={handleSubmit}>

                    <div className={styles.formGrid}>

                        {/* Item Name */}
                        <div className={styles.formGroup}>
                            <label>Item Name</label>

                            <input
                                type="text"
                                name="itemName"
                                placeholder="Enter item name"
                                value={formData.itemName}
                                onChange={handleChange}
                                required
                            />
                        </div>


                        {/* Category */}
                        <div className={styles.formGroup}>
                            <label>Category</label>

                            <select
                                name="category"
                                value={formData.category}
                                onChange={handleChange}
                                required
                            >
                                <option value="">
                                    Select category
                                </option>

                                <option value="Dairy">Dairy</option>
                                <option value="Fruits">Fruits</option>
                                <option value="Vegetables">
                                    Vegetables
                                </option>
                                <option value="Bakery">Bakery</option>
                                <option value="Pantry">Pantry</option>
                                <option value="Beverages">
                                    Beverages
                                </option>
                                <option value="Frozen">Frozen</option>
                                <option value="Other">Other</option>
                            </select>
                        </div>


                        {/* Quantity */}
                        <div className={styles.formGroup}>
                            <label>Quantity</label>

                            <input
                                type="number"
                                name="quantity"
                                placeholder="Enter quantity"
                                min="1"
                                value={formData.quantity}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        {/* unit */}
                        <div className={styles.formGroup}>
                            <label>Unit</label>
                            <select
                                name="unit"
                                value={formData.unit}
                                onChange={handleChange}
                                required>
                                    <option value="">Select unit</option>
                                    <option value="pieces">Pieces</option>
                                    <option value="kg">Kg</option>
                                    <option value="g">Grams</option>
                                    <option value="litre">Litre</option>
                                    <option value="ml">Millilitre</option>
                                    <option value="packet">Packet</option>
                                    <option value="bottle">Bottle</option>
                                    <option value="box">Box</option>
                            </select>
                        </div>
                        {/* Price */}
                        <div className={styles.formGroup}>
                            <label>Price</label>

                            <input
                                type="number"
                                name="price"
                                placeholder="Enter price"
                                min="0"
                                value={formData.price}
                                onChange={handleChange}
                            />
                        </div>


                        {/* Purchased Date */}
                        <div className={styles.formGroup}>
                            <label>Purchased Date</label>

                            <input
                                type="date"
                                name="purchasedDate"
                                value={formData.purchasedDate}
                                onChange={handleChange}
                                required
                            />
                        </div>


                        {/* Expiry Date */}
                        <div className={styles.formGroup}>
                            <label>Expiry Date</label>

                            <input
                                type="date"
                                name="expiryDate"
                                value={formData.expiryDate}
                                onChange={handleChange}
                                required
                            />
                        </div>


                        {/* Storage Location */}
                        <div
                            className={`${styles.formGroup} ${styles.fullWidth}`}
                        >
                            <label>Storage Location</label>

                            <select
                                name="storageLocation"
                                value={formData.storageLocation}
                                onChange={handleChange}
                                required
                            >
                                <option value="">
                                    Select storage location
                                </option>

                                <option value="Refrigerator">
                                    Refrigerator
                                </option>

                                <option value="Freezer">
                                    Freezer
                                </option>

                                <option value="Pantry">
                                    Pantry
                                </option>

                                <option value="Kitchen Counter">
                                    Kitchen Counter
                                </option>

                                <option value="Other">
                                    Other
                                </option>
                            </select>
                        </div>


                        {/* Notes */}
                        <div
                            className={`${styles.formGroup} ${styles.fullWidth}`}
                        >
                            <label>Notes</label>

                            <textarea
                                name="notes"
                                placeholder="Add any additional notes..."
                                value={formData.notes}
                                onChange={handleChange}
                                rows="4"
                            />
                        </div>

                    </div>


                    <div className={styles.actions}>

                        <button
                            type="button"
                            className={styles.cancelButton}
                            onClick={() => navigate("/dashboard")}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className={styles.submitButton}
                            onClick={handleSubmit}
                        >
                            + Add Item
                        </button>

                    </div>

                </form>

            </div>

        </div>
    );
}