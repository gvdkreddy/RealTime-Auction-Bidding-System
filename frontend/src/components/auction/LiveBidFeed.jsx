import { formatCurrency } from "../../utils/formatCurrency";
import { formatDate } from "../../utils/formatDate";

export default function LiveBidFeed({ bids }) {
  return (
    <div className="panel">
      <div className="panel-heading"><h3>Live activity</h3><span className="live-dot">● LIVE</span></div>
      <div className="bid-list">
        {bids.slice(0, 8).map((bid, i) => (
          <div className="bid-row" key={bid.bidId ?? `${bid.createdAt}-${i}`}>
            <div><strong>{bid.bidderName}</strong><small>{formatDate(bid.createdAt)}</small></div>
            <strong>{formatCurrency(bid.amount)}</strong>
          </div>
        ))}
      </div>
    </div>
  );
}
