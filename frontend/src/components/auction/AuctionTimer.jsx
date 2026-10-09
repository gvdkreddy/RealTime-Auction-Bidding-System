import { useEffect, useMemo, useState } from "react";

function getRemaining(target) {
  const diff = new Date(target).getTime() - Date.now();
  return Math.max(0, diff);
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

export default function AuctionTimer({ target, label = "Ends in" }) {
  const [remaining, setRemaining] = useState(() => getRemaining(target));

  useEffect(() => {
    const timer = setInterval(() => setRemaining(getRemaining(target)), 1000);
    return () => clearInterval(timer);
  }, [target]);

  const p = useMemo(() => parts(remaining), [remaining]);

  if (remaining <= 0) return <div className="timer expired">{label}: 00:00:00</div>;

  return (
    <div className="timer">
      <span>{label}</span>
      <strong>{p.d ? `${p.d}d ` : ""}{String(p.h).padStart(2,"0")}:{String(p.m).padStart(2,"0")}:{String(p.s).padStart(2,"0")}</strong>
    </div>
  );
}
