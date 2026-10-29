import { useEffect, useState } from "react";
import styles from "../styling/reports.module.css";
import axios from "../utils/apiClient";
import {
    PieChart,
    Pie,
    Cell,
    LineChart,
    Line,
    Legend,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
} from "recharts";

export default function Reports() {
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [categoryWaste, setCategoryWaste] = useState([]);
    const [mostWastedItems, setMostWastedItems] = useState([]);
    const [monthlyData, setMonthlyData] = useState([]);
    const [inventoryStatus, setInventoryStatus] = useState([]);

    useEffect(() => {
        const reportData = async () => {
            try {
                const response = await axios.get("/api/reports/summary");
                setReport(response.data);
                setLoading(false);
            } catch (error) {
                console.error("Error fetching inventory data for Reports:", error);
                setLoading(false);
                setError("Failed to fetch reports. Please try again later.");
            }
        };
        reportData();
    }, []);

    useEffect(() => {
        axios.get("/api/reports/waste-by-category")
            .then(({ data }) => {
                console.log("Category waste:", data);
                setCategoryWaste(data);
            })
            .catch((error) => {
                console.error("Category waste error:", error);
            });
    }, []);

    useEffect(() => {
        axios.get("/api/reports/most-wasted")
            .then(({ data }) => {
                console.log("Most wasted items:", data);
                setMostWastedItems(data);
            })
            .catch((error) => {
                console.error("Most wasted items error:", error);
            });
    }, []);


    useEffect(() => {
        axios.get("/api/reports/monthly")
            .then(({ data }) => {
                console.log("Monthly report:", data);
                setMonthlyData(data);
            })
            .catch((error) => {
                console.error("Monthly report error:", error);
            });
    }, []);


    useEffect(() => {
        axios.get("/api/reports/inventory-status")
            .then(({ data }) => {
                console.log("Inventory status:", data);
                setInventoryStatus(data);
            })
            .catch((error) => {
                console.error("Inventory status error:", error);
            });
    }, []);

    if (loading) {
        return <div className={styles.message}>Loading reports...</div>;
    }

    if (error) {
        return <div className={styles.message}>{error}</div>;
    }

    return (
        <div className={styles.reportsPage}>
            <div className={styles.pageHeader}>
                <div>
                    <h1>Reports & Analytics</h1>
                    <p>
                        Understand your grocery spending, inventory value and food waste.
                    </p>
                </div>
            </div>

            {/* Summary Cards */}
            <div className={styles.summaryGrid}>
                <div className={styles.summaryCard}>
                    <div className={styles.cardTitle}>Total Spending</div>
                    <div className={styles.cardValue}>
                        ₹{report.totalSpending?.toFixed(2) || "0.00"}
                    </div>
                    <div className={styles.cardDescription}>
                        Total value of groceries purchased
                    </div>
                </div>

                <div className={styles.summaryCard}>
                    <div className={styles.cardTitle}>Wasted Amount</div>
                    <div className={styles.cardValue}>
                        ₹{report.expiredWaste?.toFixed(2) || "0.00"}
                    </div>
                    <div className={styles.cardDescription}>
                        Value of expired groceries
                    </div>
                </div>

                <div className={styles.summaryCard}>
                    <div className={styles.cardTitle}>Waste Percentage</div>
                    <div className={styles.cardValue}>
                        {report.wastePercentage?.toFixed(1) || "0.0"}%
                    </div>
                    <div className={styles.cardDescription}>
                        Percentage of spending that was wasted
                    </div>
                </div>

                <div className={styles.summaryCard}>
                    <div className={styles.cardTitle}>
                        Potential Savings
                    </div>
                    <div className={styles.cardValue}>
                        ₹{report.potentialSavings?.toFixed(2) || "0.00"}
                    </div>
                    <div className={styles.cardDescription}>
                        Estimated value you can save by using expiring items
                    </div>
                </div>

            </div>

            {/* Inventory Value */}
            <div className={styles.section}>
                <h2>Inventory Value</h2>

                <div className={styles.inventoryValueGrid}>
                    <div className={styles.valueCard}>
                        <span>Fresh Inventory</span>
                        <strong>₹{report.freshInventoryValue?.toFixed(2) || "0.00"}</strong>
                    </div>

                    <div className={styles.valueCard}>
                        <span>Expiring Soon</span>
                        <strong>₹{report.expiringValue?.toFixed(2) || "0.00"}</strong>
                    </div>

                    <div className={styles.valueCard}>
                        <span>Expired</span>
                        <strong>₹{report.expiredWaste?.toFixed(2) || "0.00"}</strong>
                    </div>
                </div>
            </div>

            {/* ==================================================
    THREE ANALYTICS CHARTS
================================================== */}

            <div className={styles.analyticsGrid}>

                {/* Spending & Waste Trend */}
                <div className={styles.analyticsCard}>

                    <h2>Spending & Waste Trend</h2>

                    {monthlyData.length > 0 ? (

                        <ResponsiveContainer width="100%" height={280}>

                            <LineChart
                                data={monthlyData}
                                margin={{
                                    top: 10,
                                    right: 10,
                                    left: 0,
                                    bottom: 10
                                }}
                            >

                                <CartesianGrid strokeDasharray="3 3" />

                                <XAxis dataKey="month" />

                                <YAxis />

                                <Tooltip
                                    formatter={(value) =>
                                        `₹${Number(value).toFixed(2)}`
                                    }
                                />

                                <Legend />

                                <Line
                                    type="monotone"
                                    dataKey="spending"
                                    name="Spending"
                                    stroke="#16861c"
                                    strokeWidth={2}
                                    dot={{ r: 4 }}
                                />

                                <Line
                                    type="monotone"
                                    dataKey="waste"
                                    name="Waste"
                                    stroke="#e7c145"
                                    strokeWidth={2}
                                    dot={{ r: 4 }}
                                />

                            </LineChart>

                        </ResponsiveContainer>

                    ) : (

                        <div className={styles.chartPlaceholder}>
                            No monthly data available yet.
                        </div>

                    )}

                </div>


                {/* Inventory Status */}
                <div className={styles.analyticsCard}>

                    <h2>Inventory Status</h2>

                    {inventoryStatus.length > 0 ? (

                        <ResponsiveContainer width="100%" height={280}>

                            <PieChart>

                                <Pie
                                    data={inventoryStatus}
                                    dataKey="value"
                                    nameKey="status"
                                    cx="50%"
                                    cy="45%"
                                    outerRadius={85}
                                    innerRadius={55}
                                    paddingAngle={3}
                                    label
                                >

                                    {inventoryStatus.map((entry, index) => (

                                        <Cell
                                            key={`cell-${index}`}
                                            fill={
                                                index === 0
                                                    ? "#16861c"
                                                    : index === 1
                                                        ? "#e7c145"
                                                        : "#d9534f"
                                            }
                                        />

                                    ))}

                                </Pie>

                                <Tooltip
                                    formatter={(value) =>
                                        `₹${Number(value).toFixed(2)}`
                                    }
                                />

                                <Legend />

                            </PieChart>

                        </ResponsiveContainer>

                    ) : (

                        <div className={styles.chartPlaceholder}>
                            No inventory data available.
                        </div>

                    )}

                </div>


                {/* Waste by Category */}
                <div className={styles.analyticsCard}>

                    <h2>Waste by Category</h2>

                    {categoryWaste.length > 0 ? (

                        <ResponsiveContainer width="100%" height={280}>

                            <BarChart
                                data={categoryWaste}
                                margin={{
                                    top: 10,
                                    right: 10,
                                    left: 0,
                                    bottom: 10
                                }}
                            >

                                <CartesianGrid strokeDasharray="3 3" />

                                <XAxis dataKey="category" />

                                <YAxis />

                                <Tooltip
                                    formatter={(value) => [
                                        `₹${Number(value).toFixed(2)}`,
                                        "Wasted"
                                    ]}
                                />

                                <Bar
                                    dataKey="wastedValue"
                                    name="Wasted Value"
                                    fill="#16861c"
                                    radius={[6, 6, 0, 0]}
                                />

                            </BarChart>

                        </ResponsiveContainer>

                    ) : (

                        <div className={styles.chartPlaceholder}>
                            No expired items yet.
                        </div>

                    )}

                </div>

            </div>


            {/* ==================================================
    MOST WASTED ITEMS
================================================== */}

            <div className={styles.wastedItemsSection}>

                <div className={styles.analyticsCard}>

                    <h2>Most Wasted Items</h2>

                    {mostWastedItems.length > 0 ? (

                        <div className={styles.tableContainer}>

                            <table>

                                <thead>
                                    <tr>
                                        <th>Item</th>
                                        <th>Category</th>
                                        <th>Quantity</th>
                                        <th>Wasted Value</th>
                                    </tr>
                                </thead>

                                <tbody>

                                    {mostWastedItems.map((item) => (

                                        <tr key={item.id}>

                                            <td>{item.itemName}</td>

                                            <td>{item.category}</td>

                                            <td>{item.quantity}</td>

                                            <td>
                                                ₹
                                                {Number(
                                                    item.wastedValue
                                                ).toFixed(2)}
                                            </td>

                                        </tr>

                                    ))}

                                </tbody>

                            </table>

                        </div>

                    ) : (

                        <div className={styles.chartPlaceholder}>
                            No wasted items yet.
                        </div>

                    )}

                </div>

            </div>
        </div>

    );
}
