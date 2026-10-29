import styles from "../styling/Dashboard.module.css";
import SummaryCards from "../components/SummaryCards";
import InventoryOverview from "../components/InventoryOverview";
import { useInventory } from "../context/InventoryContext";
export default function Dashboard() {
  const {inventory} = useInventory();
  
  
  const freshCount = inventory.filter((item) => item.status === "FRESH").length;

  const expiringCount = inventory.filter(
    (item) => item.status === "EXPIRING",
  ).length;

  const expiredCount = inventory.filter(
    (item) => item.status === "EXPIRED",
  ).length;

  // rest of Dashboard...

  return (
    <div className={styles.dashboardContent}>
      <SummaryCards freshCount={freshCount} expiringCount={expiringCount} expiredCount={expiredCount} />
      <InventoryOverview/>
    </div>
  );
}
