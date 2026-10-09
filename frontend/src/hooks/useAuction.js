import { useCallback, useEffect, useState } from "react";
import { getAuction } from "../services/auctionService";
import { getAuctionBids } from "../services/bidService";

export default function useAuction(id) {
  const [auction, setAuction] = useState(null);
  const [bids, setBids] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [auctionData, bidData] = await Promise.all([
        getAuction(id),
        getAuctionBids(id)
      ]);
      setAuction(auctionData);
      setBids(Array.isArray(bidData) ? bidData : []);
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Unable to load auction.");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => { load(); }, [load]);

  return { auction, setAuction, bids, setBids, loading, error, reload: load };
}
