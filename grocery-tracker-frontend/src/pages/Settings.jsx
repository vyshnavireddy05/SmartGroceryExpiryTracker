
import styles from '../styling/Settings.module.css';
import { useCallback, useEffect, useState } from 'react';
import axios from '../utils/apiClient';
import { useNavigate } from 'react-router-dom';

export default function Settings() {
    const navigate = useNavigate();

    const [expiryRemainderDays, setExpiryRemainderDays] = useState(7);
    const [notificationsEnabled, setNotificationsEnabled] = useState(true);

    const [profile, setProfile] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: ''
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    // Read the JWT saved after login.
    const getAuthHeaders = useCallback(() => {
        const token = localStorage.getItem('token');

        if (!token) {
            navigate('/login');
            throw new Error('Authentication token not found');
        }

        return {
            Authorization: `Bearer ${token}`
        };
    }, [navigate]);

    // Fetch settings for the authenticated user.
    useEffect(() => {
        const getDetails = async () => {
            try {
                const response = await axios.get(
                    '/api/settings/Details',
                    {
                        headers: getAuthHeaders()
                    }
                );

                const data = response.data;

                setProfile({
                    firstName: data.firstName ?? '',
                    lastName: data.lastName ?? '',
                    email: data.email ?? '',
                    phoneNumber: data.phoneNum ?? ''
                });

                setNotificationsEnabled(
                    data.notificationsEnabled ?? true
                );

                setExpiryRemainderDays(
                    data.expiryRemainderDays ?? 7
                );
            } catch (error) {
                console.error(
                    'Error fetching settings:',
                    error.response?.data || error.message
                );

                if (error.response?.status === 401) {
                    localStorage.removeItem('token');
                    navigate('/login');
                    return;
                }

                if (error.message !== 'Authentication token not found') {
                    alert('Unable to load your settings. Please try again.');
                }
            } finally {
                setLoading(false);
            }
        };

        getDetails();
    }, [getAuthHeaders, navigate]);

    // Handle profile input changes.
    const handleProfileChange = (e) => {
        const { name, value } = e.target;

        setProfile((previousProfile) => ({
            ...previousProfile,
            [name]: value
        }));
    };

    // Save settings for the authenticated user.
    const handleSave = async () => {
        if (loading || saving) return;

        setSaving(true);

        try {
            const response = await axios.put(
                '/api/settings/update',
                {
                    firstName: profile.firstName,
                    lastName: profile.lastName,
                    email: profile.email,
                    phoneNum: profile.phoneNumber,
                    notificationsEnabled,
                    expiryRemainderDays
                },
                {
                    headers: getAuthHeaders()
                }
            );

            const data = response.data;

            setProfile({
                firstName: data.firstName ?? '',
                lastName: data.lastName ?? '',
                email: data.email ?? '',
                phoneNumber: data.phoneNum ?? ''
            });

            setNotificationsEnabled(
                data.notificationsEnabled ?? false
            );

            setExpiryRemainderDays(
                data.expiryRemainderDays ?? 7
            );

            alert('Settings saved successfully!');
            navigate('/dashboard');
        } catch (error) {
            console.error(
                'Error saving settings:',
                error.response?.data || error.message
            );

            if (error.response?.status === 401) {
                localStorage.removeItem('token');
                navigate('/login');
                return;
            }

            if (error.message !== 'Authentication token not found') {
                alert('Failed to save settings. Please try again.');
            }
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className={styles.settingsPage}>
                <div className={styles.header}>
                    <h1>Settings</h1>
                    <p>Loading your settings...</p>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.settingsPage}>
            <div className={styles.header}>
                <h1>Settings</h1>
                <p>Manage your profile and application preferences.</p>
            </div>

            {/* Profile Information */}
            <section className={styles.settingsSection}>
                <div className={styles.sectionHeader}>
                    <h2>Profile Information</h2>
                    <p>Update your personal information.</p>
                </div>

                <div className={styles.formGrid}>
                    <div className={styles.formGroup}>
                        <label htmlFor="firstName">First Name</label>
                        <input
                            id="firstName"
                            name="firstName"
                            type="text"
                            placeholder="First Name"
                            value={profile.firstName}
                            onChange={handleProfileChange}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="lastName">Last Name</label>
                        <input
                            id="lastName"
                            name="lastName"
                            type="text"
                            placeholder="Last Name"
                            value={profile.lastName}
                            onChange={handleProfileChange}
                        />
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="email">Email</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={profile.email}
                            readOnly
                            aria-describedby="emailHelp"
                        />
                        <small id="emailHelp">
                            Email changes are not available yet.
                        </small>
                    </div>

                    <div className={styles.formGroup}>
                        <label htmlFor="phoneNumber">Phone Number</label>
                        <input
                            id="phoneNumber"
                            name="phoneNumber"
                            type="tel"
                            placeholder="Phone Number"
                            value={profile.phoneNumber}
                            onChange={handleProfileChange}
                        />
                    </div>
                </div>
            </section>

            {/* Notifications */}
            <section className={styles.settingsSection}>
                <div className={styles.sectionHeader}>
                    <h2>Notifications</h2>
                    <p>Manage expiry notifications.</p>
                </div>

                <div className={styles.settingRow}>
                    <div>
                        <h3>Expiry Notifications</h3>
                        <p>
                            Receive notifications when your grocery items
                            are approaching their expiry date.
                        </p>
                    </div>

                    <button
                        type="button"
                        aria-label="Toggle expiry notifications"
                        aria-pressed={notificationsEnabled}
                        className={`${styles.toggle} ${
                            notificationsEnabled ? styles.toggleOn : ''
                        }`}
                        onClick={() =>
                            setNotificationsEnabled(
                                (previous) => !previous
                            )
                        }
                    >
                        <span></span>
                    </button>
                </div>
            </section>

            {/* Expiry Reminder */}
            <section className={styles.settingsSection}>
                <div className={styles.sectionHeader}>
                    <h2>Expiry Reminder</h2>
                    <p>
                        Choose when you want to be reminded about
                        expiring items.
                    </p>
                </div>

                <div className={styles.reminderRow}>
                    <label htmlFor="expiryRemainderDays">
                        Remind me before expiry
                    </label>

                    <select
                        id="expiryRemainderDays"
                        value={expiryRemainderDays}
                        onChange={(e) =>
                            setExpiryRemainderDays(
                                Number(e.target.value)
                            )
                        }
                    >
                        <option value={3}>3 days</option>
                        <option value={5}>5 days</option>
                        <option value={7}>7 days</option>
                        <option value={10}>10 days</option>
                    </select>
                </div>
            </section>

            {/* Save Button */}
            <div className={styles.saveButtonContainer}>
                <button
                    type="button"
                    className={styles.saveButton}
                    onClick={handleSave}
                    disabled={saving || loading}
                >
                    {saving ? 'Saving...' : 'Save Changes'}
                </button>
            </div>
        </div>
    );
}