
import { useEffect, useMemo, useState } from "react";

function parseAuctionTime(target) {
  if (!target) return NaN;

  // Explicit timezone (Z or ±HH:mm): respect it.
  if (/[zZ]$|[+-]\d{2}:\d{2}$/.test(target)) {
    return new Date(target).getTime();
  }

  // Backend LocalDateTime has no timezone.
  // Interpret it as India Standard Time (UTC+05:30).
  return new Date(`${target}+05:30`).getTime();
}

function getRemaining(target) {
  const timestamp = parseAuctionTime(target);
  if (!Number.isFinite(timestamp)) return 0;

  return Math.max(0, timestamp - Date.now());
}

function parts(ms) {
  const total = Math.floor(ms / 1000);

  return {
    d: Math.floor(total / 86400),
    h: Math.floor((total % 86400) / 3600),
    m: Math.floor((total % 3600) / 60),
    s: total % 60
  };
}

export default function AuctionTimer({
  target,
  label = "Ends in"
}) {
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    const timer = setInterval(() => {
      setNow(Date.now());
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const remaining = useMemo(
    () => getRemaining(target),
    [target, now]
  );

  const p = useMemo(() => parts(remaining), [remaining]);

  if (!target || !Number.isFinite(parseAuctionTime(target))) {
    return <div className="timer expired">{label}: Time unavailable</div>;
  }

  if (remaining <= 0) {
    return <div className="timer expired">{label}: 00:00:00</div>;
  }

  return (
    <div className="timer">
      <span>{label}</span>
      <strong>
        {p.d ? `${p.d}d ` : ""}
        {String(p.h).padStart(2, "0")}:
        {String(p.m).padStart(2, "0")}:
        {String(p.s).padStart(2, "0")}
      </strong>
    </div>
  );
}
