import styles from "../styling/Profile.module.css";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";

function Profile() {

    const navigate = useNavigate();

    const [profile, setProfile] = useState({
        firstName: "",
        lastName: "",
        email: "",
        phoneNumber: "",
        notificationsEnabled: true,
        expiryRemainderDays: 7
    });

    useEffect(() => {

        const getProfileDetails = async () => {

            try {

                const token = localStorage.getItem("token");

                if (!token) {
                    navigate("/login");
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
                console.log("Api Response:", response);
                console.log("Profile details fetched:", response.data);
                const data = response.data;

                setProfile({
                    firstName: data.firstName,
                    lastName: data.lastName,
                    email: data.email,
                    phoneNumber: data.phoneNum,
                    notificationsEnabled:
                        data.notificationsEnabled,
                    expiryRemainderDays:
                        data.expiryRemainderDays
                });


            } catch (error) {

                console.error(
                    "Error fetching profile details:",
                    error
                );

            }
        };

        getProfileDetails();

    }, []);

    const fullName =
        `${profile.firstName} ${profile.lastName}`.trim();

    const firstLetter =
        profile.firstName
            ? profile.firstName.charAt(0).toUpperCase()
            : "U";

    return (
        <div className={styles.profilePage}>

            {/* Page Header */}
            <div className={styles.pageHeader}>

                <h1>Profile</h1>

                <p>
                    View and manage your account information.
                </p>

            </div>


            {/* Profile Overview */}
            <section className={styles.profileOverview}>

                <div className={styles.profileIdentity}>

                    <div className={styles.avatar}>
                        {firstLetter}
                    </div>

                    <div className={styles.identityDetails}>

                        <h2>
                            {fullName || "User"}
                        </h2>

                        <p>
                            {profile.email}
                        </p>

                        <span>
                            {profile.phoneNumber}
                        </span>

                    </div>

                </div>


                <button
                    className={styles.editButton}
                    onClick={() =>
                        navigate('/dashboard/settings')
                    }
                >
                    Edit Profile
                </button>

            </section>


            {/* Contact Information */}
            <section className={styles.card}>

                <div className={styles.cardHeader}>

                    <h2>Contact Information</h2>

                    <p>
                        Your registered account details.
                    </p>

                </div>


                <div className={styles.infoGrid}>

                    {/* First Name */}
                    <div className={styles.infoItem}>

                        <div className={styles.infoIcon}>
                            👤
                        </div>

                        <div>

                            <span>First Name</span>

                            <strong>
                                {profile.firstName}
                            </strong>

                        </div>

                    </div>


                    {/* Last Name */}
                    <div className={styles.infoItem}>

                        <div className={styles.infoIcon}>
                            👤
                        </div>

                        <div>

                            <span>Last Name</span>

                            <strong>
                                {profile.lastName}
                            </strong>

                        </div>

                    </div>


                    {/* Email */}
                    <div className={styles.infoItem}>

                        <div className={styles.infoIcon}>
                            ✉
                        </div>

                        <div>

                            <span>Email</span>

                            <strong>
                                {profile.email}
                            </strong>

                        </div>

                    </div>


                    {/* Phone */}
                    <div className={styles.infoItem}>

                        <div className={styles.infoIcon}>
                            ☎
                        </div>

                        <div>

                            <span>Phone Number</span>

                            <strong>
                                {profile.phoneNumber}
                            </strong>

                        </div>

                    </div>

                </div>

            </section>


            {/* Account Preferences */}
            <section className={styles.card}>

                <div className={styles.cardHeader}>

                    <h2>Account Preferences</h2>

                    <p>
                        Your current grocery reminder preferences.
                    </p>

                </div>


                <div className={styles.preferenceList}>

                    {/* Notifications */}
                    <div className={styles.preferenceItem}>

                        <div>

                            <strong>
                                Expiry Notifications
                            </strong>

                            <p>
                                Receive notifications when items
                                are approaching expiry.
                            </p>

                        </div>

                        <span
                            className={
                                profile.notificationsEnabled
                                    ? styles.enabledBadge
                                    : styles.reminderBadge
                            }
                        >
                            {profile.notificationsEnabled
                                ? "Enabled"
                                : "Disabled"}
                        </span>

                    </div>


                    {/* Reminder */}
                    <div className={styles.preferenceItem}>

                        <div>

                            <strong>
                                Expiry Reminder
                            </strong>

                            <p>
                                Get reminded before your grocery
                                items expire.
                            </p>

                        </div>

                        <span className={styles.reminderBadge}>

                            {profile.expiryRemainderDays} days

                        </span>

                    </div>

                </div>

            </section>

        </div>
    );
}

export default Profile;