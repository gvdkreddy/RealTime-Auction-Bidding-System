import { Client } from "@stomp/stompjs";

export function createStompClient({ onConnect, onDisconnect, onError }) {
  const client = new Client({
    brokerURL: import.meta.env.VITE_WS_URL || "ws://auctionhub-backend-gyac.onrender.com/ws",
    reconnectDelay: 3000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    debug: () => {}
  });

  client.onConnect = onConnect;
  client.onDisconnect = onDisconnect;
  client.onStompError = (frame) => onError?.(frame.headers?.message || "WebSocket broker error");
  client.onWebSocketError = () => onError?.("WebSocket connection failed.");

  return client;
}
