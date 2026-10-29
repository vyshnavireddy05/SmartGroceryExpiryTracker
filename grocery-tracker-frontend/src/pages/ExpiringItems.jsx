import InventoryOverview from "../components/InventoryOverview";

export default function ExpiringItems() {

    return (
        <div>
            <InventoryOverview filter="expiring" />
        </div>
    );
}