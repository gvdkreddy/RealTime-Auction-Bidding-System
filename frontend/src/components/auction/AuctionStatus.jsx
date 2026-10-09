const labels = { UPCOMING: "Upcoming", LIVE: "Live", ENDED: "Ended", CANCELLED: "Cancelled" };

export default function AuctionStatus({ status }) {
  return <span className={`status status-${String(status || "").toLowerCase()}`}>{labels[status] || status || "Unknown"}</span>;
}
