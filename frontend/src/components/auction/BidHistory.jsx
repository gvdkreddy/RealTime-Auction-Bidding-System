import { formatCurrency } from "../../utils/formatCurrency";
import { formatDate } from "../../utils/formatDate";

export default function BidHistory({ bids }) {
  return (
    <div className="panel">
      <div className="panel-heading"><h3>Bid history</h3><span>{bids.length} bids</span></div>
      {!bids.length ? <p className="muted">No bids yet.</p> : (
        <div className="bid-list">
          {bids.map((bid, i) => (
            <div className="bid-row" key={bid.bidId ?? `${bid.createdAt}-${i}`}>
              <div><strong>{bid.bidderName}</strong><small>{formatDate(bid.createdAt)}</small></div>
              <strong>{formatCurrency(bid.amount)}</strong>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
