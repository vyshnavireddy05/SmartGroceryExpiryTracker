import { Outlet } from "react-router-dom";
import DashboardSidebar from "./DashBoardSideBar";
import styles from '../styling/DashBoardLayout.module.css'
export default function DashBoardLayout() {
    return(
        <div className={styles.appLayout}>
            <div className={styles.dashboardArea}>
                <DashboardSidebar/>
                <main className={styles.contentArea}>
                    <Outlet/>
                </main>
            </div>
        </div>
    )
}