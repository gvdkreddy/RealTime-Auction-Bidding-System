import { Link } from "react-router-dom";
import AuctionStatus from "../auction/AuctionStatus";
import { formatCurrency } from "../../utils/formatCurrency";

export default function SellerAuctionCard({ auction }) {
  return (
    <div className="seller-row">
      <div><strong>{auction.title}</strong><small>#{auction.id}</small></div>
      <span>{formatCurrency(auction.currentPrice)}</span>
      <AuctionStatus status={auction.status} />
      <Link className="btn small secondary" to={`/auctions/${auction.id}`}>Open</Link>
    </div>
  );
}
