import AuctionCard from "./AuctionCard";
import EmptyState from "../common/EmptyState";

export default function AuctionGrid({ auctions }) {
  if (!auctions.length) return <EmptyState title="No auctions found" text="Create an auction or check back later." />;
  return <div className="auction-grid">{auctions.map((a) => <AuctionCard key={a.id} auction={a} />)}</div>;
}
