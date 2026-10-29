import InventoryOverview from "../components/InventoryOverview";

export default function ExpiredItems() {

    return (
        <div>
            <InventoryOverview filter="expired" />
        </div>
    );
}