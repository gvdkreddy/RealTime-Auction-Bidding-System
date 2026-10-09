import api from "./api";

export async function getMe() {
  const { data } = await api.get("/api/users/me");
  return data;
}

export async function depositWallet(amount) {
  const { data } = await api.post("/api/wallet/deposit", { amount });
  return data;
}
