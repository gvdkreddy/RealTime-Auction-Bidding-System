import { useEffect, useState } from "react";
import { connectAuctionSocket } from "../websocket/auctionSocket";

export default function useWebSocket(auctionId, { onBid, onStatus } = {}) {
  const [connected, setConnected] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!auctionId) return undefined;

    const disconnect = connectAuctionSocket({
      auctionId,
      onBid,
      onStatus,
      onConnected: () => {
        setConnected(true);
        setError("");
      },
      onError: (message) => {
        setConnected(false);
        setError(message);
      }
    });

    return () => {
      setConnected(false);
      disconnect?.();
    };
  }, [auctionId, onBid, onStatus]);

  return { connected, error };
}
