import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { getAuctions } from "../services/auctionService";
import AuctionGrid from "../components/auction/AuctionGrid";

export default function Home() {
  const [auctions, setAuctions] = useState([]);

  useEffect(() => {
    getAuctions().then((data) => setAuctions(Array.isArray(data) ? data.slice(0, 3) : [])).catch(() => {});
  }, []);

  return (
    <>
      <section className="hero">
        <div>
          <span className="eyebrow">REAL-TIME AUCTIONS</span>
          <h1>Bid live.<br /><span>Win confidently.</span></h1>
          <p>Discover live auctions, place secure bids, and watch every bid update in real time.</p>
          <div className="hero-actions">
            <Link className="btn primary large" to="/auctions">Explore auctions</Link>
            <Link className="btn secondary large" to="/create-auction">Sell an item</Link>
          </div>
        </div>
        <div className="hero-card">
          <div className="pulse-ring"><span>LIVE</span></div>
          <h3>Live bidding</h3>
          <p>WebSocket-powered updates mean you never need to refresh to see the latest bid.</p>
        </div>
      </section>
      <section className="section">
        <div className="section-heading"><div><span className="eyebrow">MARKETPLACE</span><h2>Featured auctions</h2></div><Link to="/auctions">View all →</Link></div>
        <AuctionGrid auctions={auctions} />
      </section>
    </>
  );
}
