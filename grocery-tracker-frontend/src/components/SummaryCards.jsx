import { Link } from 'react-router-dom';
import styles from '../styling/SummaryCards.module.css';
import { useNavigate } from 'react-router-dom';
export default function SummaryCards({freshCount, expiringCount, expiredCount}) {
    const navigate = useNavigate();
    return(
        <div className={styles.summaryCards}>

            {/* Fresh Items */}
            <div className={`${styles.card} ${styles.freshCard}`}>
                <div className={styles.cardTop}>
                    <div className={styles.iconCircle}>
                        🌿
                    </div>

                    <div className={styles.cardInfo}>
                        <h3>Fresh Items</h3>
                        <p className={styles.count}>{freshCount}</p>
                        <p className={styles.description}>
                            Safe to consume
                        </p>
                    </div>
                </div>

                <button className={styles.viewButton} >
                    <Link to="/dashboard/inventory/fresh" className={styles.viewLink}>
                        View all
                    </Link>
                    <span>→</span>
                </button>
            </div>


            {/* Expiring Soon */}
            <div className={`${styles.card} ${styles.expiringCard}`}>
                <div className={styles.cardTop}>
                    <div className={styles.iconCircle}>
                        ⏰
                    </div>

                    <div className={styles.cardInfo}>
                        <h3>Expiring Soon</h3>
                        <p className={styles.count}>{expiringCount}</p>
                        <p className={styles.description}>
                            Use within 3 days
                        </p>
                    </div>
                </div>

                <button className={styles.viewButton}>
                    <Link to="/dashboard/inventory/expiring" className={styles.viewLink}>
                        View all
                    </Link>
                    <span>→</span>
                </button>
            </div>


            {/* Expired Items */}
            <div className={`${styles.card} ${styles.expiredCard}`}>
                <div className={styles.cardTop}>
                    <div className={styles.iconCircle}>
                        🗑️
                    </div>

                    <div className={styles.cardInfo}>
                        <h3>Expired Items</h3>
                        <p className={styles.count}>{expiredCount}</p>
                        <p className={styles.description}>
                            Needs attention
                        </p>
                    </div>
                </div>

                <button className={styles.viewButton}>
                    
                    <Link to="/dashboard/inventory/expired" className={styles.viewLink}>
                        View all
                    </Link>
                    <span>→</span>
                </button>
            </div>

        </div>
    );
}