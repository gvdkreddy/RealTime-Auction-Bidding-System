import SellerAuctionCard from "./SellerAuctionCard";
import EmptyState from "../common/EmptyState";

export default function SellerAuctionList({ auctions }) {
  if (!auctions.length) return <EmptyState title="No auctions yet" text="Create your first auction." />;
  return <div className="seller-list">{auctions.map((a) => <SellerAuctionCard key={a.id} auction={a} />)}</div>;
}
