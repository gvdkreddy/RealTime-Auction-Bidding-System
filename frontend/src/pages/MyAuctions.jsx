import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { getAuctions } from "../services/auctionService";
import SellerAuctionList from "../components/seller/SellerAuctionList";
import LoadingSpinner from "../components/common/LoadingSpinner";

export default function MyAuctions() {
  const { user } = useAuth();
  const [auctions, setAuctions] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => { getAuctions().then((all) => setAuctions((all || []).filter((a) => a.seller?.id === user?.id))).catch(() => {}).finally(() => setLoading(false)); }, [user?.id]);
  return <section className="page-section"><span className="eyebrow">SELLER</span><h1>My auctions</h1>{loading ? <LoadingSpinner /> : <SellerAuctionList auctions={auctions} />}</section>;
}
