import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import BalanceCard from "../components/wallet/BalanceCard";
import DepositForm from "../components/wallet/DepositForm";
import AuctionGrid from "../components/auction/AuctionGrid";
import { getAuctions } from "../services/auctionService";

export default function Dashboard() {
  const { user, refreshUser } = useAuth();
  const [auctions, setAuctions] = useState([]);

  useEffect(() => {
    getAuctions().then(setAuctions).catch(() => {});
  }, []);

  const live = auctions.filter((auction) => auction.status === "LIVE").slice(0, 3);

  return (
    <section className="page-section">
      <div className="dashboard-header">
        <div>
          <span className="eyebrow">DASHBOARD</span>
          <h1>Welcome, {user?.name || "Bidder"}</h1>
          <p className="muted">Track your balance and active auctions.</p>
        </div>
        <Link className="btn primary" to="/create-auction">Create auction</Link>
      </div>

      <BalanceCard user={user} />
      <DepositForm onDeposited={refreshUser} />

      <div className="section-heading dashboard-auctions-heading">
        <h2>Live now</h2>
        <Link to="/auctions">View all →</Link>
      </div>
      <AuctionGrid auctions={live} />
    </section>
  );
}
