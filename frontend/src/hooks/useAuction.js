import { useCallback, useEffect, useRef, useState } from "react";
import { getAuction } from "../services/auctionService";
import { getAuctionBids } from "../services/bidService";

export default function useAuction(id) {
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const hasLoaded = useRef(false);

  const load = useCallback(async () => {
    // Only show the full-page loader on the first load for this auction.
    // Background refreshes should not replace the page every few seconds.
    if (!hasLoaded.current) setLoading(true);

    try {
      const [auctionData, bidData] = await Promise.all([
        getAuction(id),
        getAuctionBids(id)
      ]);
      setAuction(auctionData);
      setBids(Array.isArray(bidData) ? bidData : []);
      setError("");
    } catch (err) {
      // Keep the last successful data visible if a background refresh fails.
      if (!hasLoaded.current) {
        setError(
          err.response?.data?.message ||
          err.message ||
          "Unable to load auction."
        );
      }
    } finally {
      hasLoaded.current = true;
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    hasLoaded.current = false;
    setAuction(null);
    setBids([]);
    setError("");
    setLoading(true);
    load();
  }, [load]);

  return { auction, setAuction, bids, setBids, loading, error, reload: load };
}
