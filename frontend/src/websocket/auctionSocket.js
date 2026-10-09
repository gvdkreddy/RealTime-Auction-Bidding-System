import { createStompClient } from "./stompClient";

export function connectAuctionSocket({ auctionId, onBid, onStatus, onConnected, onError }) {
  const client = createStompClient({
    onConnect: () => {
      onConnected?.();

      const bidSubscription = client.subscribe(`/topic/auction/${auctionId}`, (message) => {
        try {
          onBid?.(JSON.parse(message.body));
        } catch {
          onError?.("Received invalid bid message.");
        }
      });

      const statusSubscription = client.subscribe(`/topic/auction/${auctionId}/status`, (message) => {
        try {
          onStatus?.(JSON.parse(message.body));
        } catch {
          onError?.("Received invalid status message.");
        }
      });

      client.__auctionSubscriptions = [bidSubscription, statusSubscription];
    },
    onDisconnect: () => {},
    onError
  });

  client.activate();

  return () => {
    client.__auctionSubscriptions?.forEach((subscription) => subscription.unsubscribe());
    client.deactivate();
  };
}
