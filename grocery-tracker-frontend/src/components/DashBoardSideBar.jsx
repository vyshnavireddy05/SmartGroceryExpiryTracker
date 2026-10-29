import { useNavigate, useLocation } from "react-router-dom";

import {
    LuHouse,
    LuSprout,
    LuChartColumn,
    LuSettings
} from "react-icons/lu";

import styles from "../styling/DashboardSidebar.module.css";

export default function DashboardSidebar() {

    const navigate = useNavigate();
    const location = useLocation();

    const menuItems = [
        {
            name: "Dashboard",
            icon: LuHouse,
            path: "/dashboard"
        },
        {
            name: "Remedy Suggestions",
            icon: LuSprout,
            path: "/dashboard/remedies"
        },
        {
            name: "Report & Analysis",
            icon: LuChartColumn,
            path: "/dashboard/reports"
        },
        {
            name: "Settings",
            icon: LuSettings,
            path: "/dashboard/settings"
        }
    ];

    return (
        <aside className={styles.sidebar}>

            <nav className={styles.navigation}>

                {menuItems.map((item) => {

                    const Icon = item.icon;

                    const isActive =
                        location.pathname === item.path;

                    return (
                        <button
                            key={item.name}
                            className={`${styles.navItem} ${isActive ? styles.active : ""
                                }`}
                            onClick={() => navigate(item.path)}
                        >

                            <Icon className={styles.navIcon} />

                            <span>{item.name}</span>

                        </button>
                    );
                })}

            </nav>
            <div className={styles.sidebarTip}>
                <div className={styles.tipIcon}>
                    <svg
                        viewBox="0 0 64 64"
                        xmlns="http://www.w3.org/2000/svg"
                        aria-hidden="true"
                    >
                        {/* Leaves */}
                        <path
                            d="M31 31C20 29 13 22 15 12C25 12 32 19 31 31Z"
                            fill="#176B3A"
                        />

                        <path
                            d="M34 29C35 18 42 11 52 11C52 21 45 28 34 29Z"
                            fill="#23864A"
                        />

                        {/* Stem */}
                        <path
                            d="M32 30V47"
                            stroke="#176B3A"
                            strokeWidth="3"
                            strokeLinecap="round"
                        />

                        {/* Pot */}
                        <path
                            d="M15 46H49L44 57H20L15 46Z"
                            fill="#176B3A"
                        />

                        <path
                            d="M12 44H52"
                            stroke="#176B3A"
                            strokeWidth="3"
                            strokeLinecap="round"
                        />
                    </svg>
                </div>

                <div className={styles.tipText}>
                    <p>Eat healthy, waste less.</p>
                    <p>Track smart, live better.</p>
                </div>
            </div>

        </aside>
    );
}