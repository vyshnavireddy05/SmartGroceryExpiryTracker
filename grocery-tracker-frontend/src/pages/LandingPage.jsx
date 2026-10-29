import styles from '../styling/landingpage.module.css';
import { useNavigate } from 'react-router-dom';

import {
    LuBell,
    LuListChecks,
    LuPiggyBank,
    LuPresentation,
    LuRecycle
} from 'react-icons/lu';

export default function LandingPage() {
    const navigate = useNavigate();
    function handleClick() {
        navigate('/login');
    }
    return (
        <div className={styles.page}>

            {/* ================= HERO ================= */}
            <main>

                <section className={styles.hero}>

                    {/* Hero Text */}
                    <div className={styles.heroContent}>

                        <h1>
                            Reduce Food Waste,
                            <br />
                            Track Expiry Dates
                        </h1>

                        <p>
                            Efficiently manage your groceries and
                            <br />
                            receive timely reminders
                        </p>

                        <button
                            className={styles.primaryButton}
                            onClick={handleClick}
                        >
                            Start Tracking
                        </button>

                    </div>


                    {/* ================= FEATURES ================= */}
                    <div className={styles.featuresWrapper}>

                        <div className={styles.featureCard}>

                            <div className={styles.featureIcon}>
                                <LuBell />
                            </div>

                            <h3>Alerts</h3>

                            <p>
                                Receive alerts before
                                <br />
                                items expire.
                            </p>

                        </div>


                        <div className={styles.featureCard}>

                            <div className={styles.featureIcon}>
                                <LuListChecks />
                            </div>

                            <h3>Grocery Management</h3>

                            <p>
                                Easily organize and
                                <br />
                                manage your grocery list.
                            </p>

                        </div>


                        <div className={styles.featureCard}>

                            <div className={styles.featureIcon}>
                                <LuBell />
                            </div>

                            <h3>Notifications</h3>

                            <p>
                                Stay informed with
                                <br />
                                timely notifications.
                            </p>

                        </div>


                        <div className={styles.featureCard}>

                            <div className={styles.featureIcon}>
                                <LuPresentation />
                            </div>

                            <h3>Analytics</h3>

                            <p>
                                Gain insights into your
                                <br />
                                grocery habits and spending.
                            </p>

                        </div>

                    </div>

                </section>
                <div className={styles.section}>
                    <h2>Benefits</h2>
                    <div className={styles.gridTemplate}>
                        <div className={styles.iconsContainer}>
                            <LuPiggyBank className={styles.featureIcons} />
                            <h3>Save Money</h3>
                            <p>Reduce food waste and save money on groceries.</p>
                        </div>
                        <div className={styles.iconsContainer}>
                            <LuRecycle className={styles.featureIcons} />
                            <h3>Reduce Waste</h3>
                            <p>Contribute to a Sustainable future.</p>
                        </div>
                        <div className={styles.iconsContainer}>
                            <LuListChecks className={styles.featureIcons} />
                            <h3>Stay Organized</h3>
                            <p>Keep track of your groceries and expiry dates in one place.</p>
                        </div>
                    </div>
                </div>



            </main>
            <footer className={styles.footer}></footer>
        </div>
    );
}