import styles from '../styling/Navbar.module.css';
import leafLogo from '../assets/logo-leaf.png';
import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

import {
    LuUser,
    LuSearch,
    LuBell

} from 'react-icons/lu';
export default function Navbar() {
    const location = useLocation();
    const pathname = location.pathname;
    const [showNotifications, setShowNotifications] = useState(false);
    // Don't show Navbar on Login, Signup, or Dashboard profile page (covers trailing slashes or subpaths)

    const isLandingPage = pathname === '/';
    const isdashboardPage = pathname.startsWith('/dashboard');
    const navigate = useNavigate();

    const [notifications, setNotifications] = useState([]);
    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const token = localStorage.getItem("token");

                if (!token) {
                    setNotifications([]);
                    return;
                }

                const response = await axios.get(
                    "/api/notification",
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                setNotifications(response.data);
            } catch (error) {
                console.error("Error fetching notifications:", error);
            }
        };

        if (pathname.startsWith("/dashboard")) {
            fetchNotifications();
        } else {
            setNotifications([]);
        }
    }, [pathname]);
    if (
        pathname === '/login' ||
        pathname === '/Signup' ||
        pathname === '/profile' ||
        pathname.startsWith('/dashboard/profile')

    ) {
        return null;
    }

    return (
        <header className={styles.header}>

            <div className={styles.brand}>
                <img
                    className={styles.logo}
                    src={leafLogo}
                    alt="Leaf Logo"
                />

                <h2>Smart Grocery Expiry Tracker</h2>
            </div>

            {isLandingPage && (
                <button className={styles.headerButton} onClick={() => { navigate('/Signup') }}>
                    Get Started
                </button>
            )}
            {isdashboardPage && (
                <div className={styles.dashboardActions}>

                    <button
                        className={styles.addItemButton}
                        onClick={() => navigate("/dashboard/addItem")}
                    >
                        <span className={styles.plusIcon}>+</span>
                        Add Item
                    </button>


                    {/* Notification */}
                    <div className={styles.notificationContainer}>

                        <button
                            className={styles.iconButton}
                            aria-label="Notifications"
                            onClick={() => setShowNotifications(!showNotifications)}
                        >
                            <LuBell />

                            {notifications.length > 0 && (
                                <span className={styles.notificationDot}></span>
                            )}
                        </button>

                        {showNotifications && (
                            <div className={styles.notificationDropdown}>

                                <div className={styles.notificationHeader}>
                                    <h3>Notifications</h3>
                                </div>

                                {notifications.length === 0 ? (
                                    <p className={styles.noNotifications}>
                                        No new notifications
                                    </p>
                                ) : (
                                    notifications.map((notification) => (
                                        <div
                                            key={notification.id}
                                            className={styles.notificationItem}
                                        >
                                            <h4>{notification.title}</h4>

                                            <p>{notification.message}</p>

                                            <span>
                                                {new Date(
                                                    notification.createdAt
                                                ).toLocaleString()}
                                            </span>
                                        </div>
                                    ))
                                )}

                            </div>
                        )}

                    </div>


                    {/* Profile */}
                    <button
                        className={styles.profileButton}
                        onClick={() => navigate('/dashboard/profile')}
                        aria-label="Profile"
                    >
                        <LuUser />
                    </button>

                </div>
            )}
        </header>
    );
}