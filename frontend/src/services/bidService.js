import api from "./api";

export async function placeBid(auctionId, amount) {
  const { data } = await api.post(`/api/auctions/${auctionId}/bids`, { amount });
  return data;
}

export async function getAuctionBids(auctionId) {
  const { data } = await api.get(`/api/auctions/${auctionId}/bids`);
  return data;
}

// This export prevents the app from failing to load. The backend endpoint
// /api/bids/my must be implemented before the My Bids page can return data.
export async function getMyBids() {
  const { data } = await api.get("/api/bids/my");
  return data;
}
