import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyBids } from "../services/bidService";
import { formatCurrency } from "../utils/formatCurrency";
import { formatDate } from "../utils/formatDate";
import LoadingSpinner from "../components/common/LoadingSpinner";
import EmptyState from "../components/common/EmptyState";

export default function MyBids() {
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => { getMyBids().then(setBids).catch(() => setBids([])).finally(() => setLoading(false)); }, []);
  return <section className="page-section"><span className="eyebrow">ACTIVITY</span><h1>My bids</h1>{loading ? <LoadingSpinner /> : !bids.length ? <EmptyState title="No bids yet" text="Join a live auction to place your first bid." /> : <div className="table-wrap"><table><thead><tr><th>Auction</th><th>Amount</th><th>Date</th><th></th></tr></thead><tbody>{bids.map((b) => <tr key={b.bidId}><td>#{b.auctionId}</td><td><strong>{formatCurrency(b.amount)}</strong></td><td>{formatDate(b.createdAt)}</td><td><Link to={`/auctions/${b.auctionId}`}>View</Link></td></tr>)}</tbody></table></div>}</section>;
}
