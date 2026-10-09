import { useEffect, useMemo, useState } from "react";
import AuctionGrid from "../components/auction/AuctionGrid";
import LoadingSpinner from "../components/common/LoadingSpinner";
import ErrorMessage from "../components/common/ErrorMessage";
import { getAuctions } from "../services/auctionService";

export default function Auctions() {
  const [auctions, setAuctions] = useState([]);
  const [status, setStatus] = useState("ALL");
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getAuctions().then(setAuctions).catch((e) => setError(e.response?.data?.message || e.message || "Failed to load auctions.")).finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => auctions.filter((a) =>
    (status === "ALL" || a.status === status) &&
    `${a.title} ${a.description || ""}`.toLowerCase().includes(query.toLowerCase())
  ), [auctions, status, query]);

  return <section className="section page-section">
    <div className="section-heading"><div><span className="eyebrow">MARKETPLACE</span><h1>All auctions</h1></div></div>
    <div className="toolbar">
      <input placeholder="Search auctions..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <select value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="ALL">All statuses</option><option value="LIVE">Live</option><option value="UPCOMING">Upcoming</option><option value="ENDED">Ended</option><option value="CANCELLED">Cancelled</option>
      </select>
    </div>
    {loading ? <LoadingSpinner /> : <><ErrorMessage message={error} /><AuctionGrid auctions={filtered} /></>}
  </section>;
}
