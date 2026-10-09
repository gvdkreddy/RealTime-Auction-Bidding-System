import { useState } from "react";
import { useNavigate } from "react-router-dom";
import CreateAuctionForm from "../components/seller/CreateAuctionForm";
import { createAuction } from "../services/auctionService";

export default function CreateAuction() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (form) => {
    setLoading(true); setError("");
    try {
      const auction = await createAuction({
        ...form,
        startingPrice: Number(form.startingPrice),
        minimumBidIncrement: Number(form.minimumBidIncrement),
        startTime: form.startTime,
        endTime: form.endTime
      });
      navigate(`/auctions/${auction.id}`);
    } catch (e) {
      setError(e.response?.data?.message || e.message || "Unable to create auction.");
    } finally { setLoading(false); }
  };

  return <section className="page-section narrow"><span className="eyebrow">SELL</span><h1>Create an auction</h1><p className="muted">Set your item, starting price, timing and minimum bid increment.</p><CreateAuctionForm onSubmit={submit} loading={loading} error={error} /></section>;
}
