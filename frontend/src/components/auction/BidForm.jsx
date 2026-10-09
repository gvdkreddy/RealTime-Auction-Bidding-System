import { useEffect, useState } from "react";
import { formatCurrency } from "../../utils/formatCurrency";

export default function BidForm({ auction, onBid, submitting }) {
  const minimum = Number(auction.currentPrice) + Number(auction.minimumBidIncrement);
  const [amount, setAmount] = useState(String(minimum));

  useEffect(() => setAmount(String(minimum)), [minimum]);

  const submit = async (e) => {
    e.preventDefault();
    await onBid(amount);
  };

  return (
    <form className="bid-form" onSubmit={submit}>
      <label>Your bid</label>
      <div className="bid-input-wrap">
        <span>₹</span>
        <input type="number" min={minimum} step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
      </div>
      <small className="muted">Minimum bid: {formatCurrency(minimum)}</small>
      <button className="btn primary full" disabled={submitting}>
        {submitting ? "Placing..." : "Place bid"}
      </button>
    </form>
  );
}
