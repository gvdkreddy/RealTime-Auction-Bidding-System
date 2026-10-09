
import { useCallback, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import useAuction from "../hooks/useAuction";
import useWebSocket from "../hooks/useWebSocket";
import AuctionStatus from "../components/auction/AuctionStatus";
import AuctionTimer from "../components/auction/AuctionTimer";
import BidForm from "../components/auction/BidForm";
import BidHistory from "../components/auction/BidHistory";
import LiveBidFeed from "../components/auction/LiveBidFeed";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { useAuth } from "../context/AuthContext";
import { placeBid } from "../services/bidService";
import { formatCurrency } from "../utils/formatCurrency";
import { formatDate } from "../utils/formatDate";

export default function AuctionDetails() {
  const { id } = useParams();

  const {
    auction,
    setAuction,
    bids,
    setBids,
    loading,
    error,
    reload
  } = useAuction(id);

  const { isAuthenticated, user, refreshUser } = useAuth();

  const [liveBids, setLiveBids] = useState([]);
  const [bidError, setBidError] = useState("");
  const [success, setSuccess] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const onBid = useCallback((bid) => {
    setBids((current) => [
      bid,
      ...current.filter((b) => b.bidId !== bid.bidId)
    ]);

    setLiveBids((current) => [
      bid,
      ...current.filter((b) => b.bidId !== bid.bidId)
    ]);

    setAuction((current) =>
      current
        ? { ...current, currentPrice: bid.amount }
        : current
    );
  }, [setAuction, setBids]);

  const onStatus = useCallback((event) => {
    if (event?.status) {
      setAuction((current) =>
        current
          ? { ...current, status: event.status }
          : current
      );

      if (event.status === "ENDED") {
        reload();
      }
    }
  }, [reload, setAuction]);

  const { connected, error: socketError } = useWebSocket(
    auction?.id,
    { onBid, onStatus }
  );

  useEffect(() => {
    setLiveBids([]);
    setBidError("");
    setSuccess("");
  }, [id]);

  if (loading) {
    return <LoadingSpinner text="Loading auction..." />;
  }

  if (error || !auction) {
    return (
      <section className="page-section">
        <div className="alert error">
          {error || "Auction not found."}
        </div>
        <Link to="/auctions">← Back to auctions</Link>
      </section>
    );
  }

  const isSeller =
    user?.id && auction.seller?.id === user.id;

  const isAuctionLive = auction.status === "LIVE";
  const isAuctionEnded = auction.status === "ENDED";

  const canBid =
    isAuthenticated && isAuctionLive && !isSeller;

  const submitBid = async (amount) => {
    setSubmitting(true);
    setBidError("");
    setSuccess("");

    try {
      const bid = await placeBid(id, amount);

      onBid(bid);

      setSuccess(
        `Bid of ${formatCurrency(bid.amount)} placed successfully.`
      );

      await refreshUser();
    } catch (e) {
      setBidError(
        e.response?.data?.message ||
        e.message ||
        "Unable to place bid."
      );
    } finally {
      setSubmitting(false);
    }
  };

  const visibleBids = liveBids.length ? liveBids : bids;

  return (
    <section className="page-section">
      <Link className="back-link" to="/auctions">
        ← Back to auctions
      </Link>

      <div className="detail-grid">
        <div>
          <div className="detail-header">
            <AuctionStatus status={auction.status} />

            <span
              className={
                isAuctionEnded
                  ? "socket ended"
                  : isAuctionLive && connected
                    ? "socket connected"
                    : "socket"
              }
            >
              {isAuctionEnded
                ? "● Auction ended"
                : isAuctionLive
                  ? connected
                    ? "● Live connection"
                    : "○ Connecting..."
                  : auction.status === "UPCOMING"
                    ? connected
                      ? "● Connected · Not started"
                      : "○ Waiting to start"
                    : connected
                      ? "● Connected · Auction inactive"
                      : "○ Disconnected"}
            </span>
          </div>

          <h1>{auction.title}</h1>

          <p className="detail-description">
            {auction.description || "No description provided."}
          </p>

          <div className="stats-grid">
            <div>
              <small>Current price</small>
              <strong>
                {formatCurrency(auction.currentPrice)}
              </strong>
            </div>

            <div>
              <small>Starting price</small>
              <strong>
                {formatCurrency(auction.startingPrice)}
              </strong>
            </div>

            <div>
              <small>Increment</small>
              <strong>
                {formatCurrency(auction.minimumBidIncrement)}
              </strong>
            </div>

            <div>
              <small>Seller</small>
              <strong>{auction.seller?.name || "—"}</strong>
            </div>
          </div>

          {isAuctionLive && (
            <AuctionTimer target={auction.endTime} />
          )}

          {auction.status === "UPCOMING" && (
            <AuctionTimer
              target={auction.startTime}
              label="Starts in"
            />
          )}

          {isAuctionEnded && (
            <div className="ended-box">
              Auction ended {formatDate(auction.endTime)}
              {auction.winner
                ? ` • Winner: ${auction.winner.name}`
                : " • No winner"}
            </div>
          )}

          {socketError && (
            <div className="alert warning">{socketError}</div>
          )}

          {success && (
            <div className="alert success">{success}</div>
          )}

          {bidError && (
            <div className="alert error">{bidError}</div>
          )}

          {canBid && (
            <div className="panel">
              <BidForm
                auction={auction}
                onBid={submitBid}
                submitting={submitting}
              />
            </div>
          )}

          {!isAuthenticated && isAuctionLive && (
            <div className="alert info">
              Please <Link to="/login">sign in</Link> to bid.
            </div>
          )}

          {isSeller && isAuctionLive && (
            <div className="alert info">
              You are the seller. You cannot bid on your own auction.
            </div>
          )}

          <BidHistory bids={visibleBids} />
        </div>

        <aside>
          <LiveBidFeed bids={visibleBids} />
        </aside>
      </div>
    </section>
  );
}
