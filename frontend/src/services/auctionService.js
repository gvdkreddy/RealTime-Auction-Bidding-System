import api from "./api";

export async function getAuctions() {
  const { data } = await api.get("/api/auctions");
  return data;
}

export async function getAuction(id) {
  const { data } = await api.get(`/api/auctions/${id}`);
  return data;
}

export async function createAuction(payload) {
  const { data } = await api.post("/api/auctions", payload);
  return data;
}

export async function startAuction(id) {
  const { data } = await api.put(`/api/auctions/${id}/start`);
  return data;
}

export async function endAuction(id) {
  const { data } = await api.put(`/api/auctions/${id}/end`);
  return data;
}
