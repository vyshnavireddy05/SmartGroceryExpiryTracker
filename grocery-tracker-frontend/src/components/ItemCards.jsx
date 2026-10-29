export default function ItemCards({ items }) {

    function calculateLabel(expiryDate){
        let currDate = new Date();
        let diff = Math.ceil((new Date(expiryDate) - currDate) / (1000*60*60*24));

        if(diff < 0){
            return "Expired";
        }else if(diff <= 7){
            return "Expiring Soon";
        }else{
            return "Fresh";
        }
    }

    return (
        <div>
            {items.map((item, index) => {
                const label = calculateLabel(item.expiryDate);

                return (
                    <div key={index}>
                        <h2>{item.name}</h2>
                        <h5>Quantity {item.quantity}</h5>
                        <p>{label}</p>
                    </div>
                );
            })}
        </div>
    );
}