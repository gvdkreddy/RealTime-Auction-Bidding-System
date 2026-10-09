import { Link } from "react-router-dom";
import AuctionStatus from "./AuctionStatus";
import AuctionTimer from "./AuctionTimer";
import { formatCurrency } from "../../utils/formatCurrency";
import { formatDate } from "../../utils/formatDate";

export default function AuctionCard({ auction }) {
  const live = auction.status === "LIVE";
  const upcoming = auction.status === "UPCOMING";

  return (
    <article className="auction-card">
      <div className="card-top">
        <AuctionStatus status={auction.status} />
        <span className="muted">#{auction.id}</span>
      </div>
      <h3>{auction.title}</h3>
      <p className="description">{auction.description || "No description provided."}</p>
      <div className="price-row">
        <div><small>Current price</small><strong>{formatCurrency(auction.currentPrice)}</strong></div>
        <div><small>Min increment</small><strong>{formatCurrency(auction.minimumBidIncrement)}</strong></div>
      </div>
      {live && <AuctionTimer target={auction.endTime} />}
      {upcoming && <AuctionTimer target={auction.startTime} label="Starts in" />}
      {!live && !upcoming && <small className="muted">{formatDate(auction.endTime)}</small>}
      <Link className="btn primary full" to={`/auctions/${auction.id}`}>View auction</Link>
    </article>
  );
}
