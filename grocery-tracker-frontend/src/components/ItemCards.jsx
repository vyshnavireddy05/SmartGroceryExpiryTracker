export default function ItemCards({ items }) {
    return (
        <div>
            {items.map((item, index) => {
                const statusLabels = {
                    FRESH: "Fresh",
                    EXPIRING: "Expiring Soon",
                    EXPIRED: "Expired",
                };
                const label = statusLabels[item.status] || "Unknown";

                return (
                    <div key={index}>
                        <h2>{item.itemName || item.name}</h2>
                        <h5>Quantity {item.quantity}</h5>
                        <p>{label}</p>
                    </div>
                );
            })}
        </div>
    );
}