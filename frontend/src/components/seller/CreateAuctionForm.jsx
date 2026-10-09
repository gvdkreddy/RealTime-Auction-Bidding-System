import { useState } from "react";

function localDateTime(minutesFromNow) {
  const d = new Date(Date.now() + minutesFromNow * 60000);
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export default function CreateAuctionForm({ onSubmit, loading, error }) {
  const [form, setForm] = useState({
    title: "",
    description: "",
    startingPrice: "",
    minimumBidIncrement: "",
    startTime: localDateTime(1),
    endTime: localDateTime(61)
  });

  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  return (
    <form className="form-card" onSubmit={(e) => { e.preventDefault(); onSubmit(form); }}>
      {error && <div className="alert error">{error}</div>}
      <div className="form-grid">
        <label>Title<input value={form.title} onChange={(e) => update("title", e.target.value)} required /></label>
        <label>Starting price<input type="number" min="0.01" step="0.01" value={form.startingPrice} onChange={(e) => update("startingPrice", e.target.value)} required /></label>
        <label>Minimum bid increment<input type="number" min="0.01" step="0.01" value={form.minimumBidIncrement} onChange={(e) => update("minimumBidIncrement", e.target.value)} required /></label>
        <label>Start time<input type="datetime-local" value={form.startTime} onChange={(e) => update("startTime", e.target.value)} required /></label>
        <label>End time<input type="datetime-local" value={form.endTime} onChange={(e) => update("endTime", e.target.value)} required /></label>
        <label className="span-2">Description<textarea rows="5" value={form.description} onChange={(e) => update("description", e.target.value)} /></label>
      </div>
      <button className="btn primary" disabled={loading}>{loading ? "Creating..." : "Create auction"}</button>
    </form>
  );
}
