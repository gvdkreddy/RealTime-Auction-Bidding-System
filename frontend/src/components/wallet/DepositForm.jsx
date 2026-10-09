import { useState } from "react";
import { depositWallet } from "../../services/userService";
import { apiErrorMessage } from "../../services/api";
import { formatCurrency } from "../../utils/formatCurrency";

const QUICK_AMOUNTS = [1000, 5000, 10000, 50000];

export default function DepositForm({ onDeposited }) {
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess("");

    const numericAmount = Number(amount);
    if (!Number.isFinite(numericAmount) || numericAmount < 1) {
      setError("Enter a deposit amount of at least ₹1.");
      return;
    }
    if (Math.round(numericAmount * 100) !== numericAmount * 100) {
      setError("Use no more than two decimal places.");
      return;
    }

    try {
      setLoading(true);
      await depositWallet(numericAmount.toFixed(2));
      if (onDeposited) await onDeposited();
      setSuccess(`${formatCurrency(numericAmount)} added to your demo wallet.`);
      setAmount("");
    } catch (requestError) {
      setError(apiErrorMessage(requestError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="deposit-card">
      <div className="deposit-heading">
        <div>
          <span className="eyebrow">DEMO WALLET</span>
          <h2>Deposit money</h2>
          <p className="muted">Add demo balance to try bidding. No real payment is made.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="deposit-form">
        <label htmlFor="deposit-amount">Amount (INR)</label>
        <div className="deposit-amount-wrap">
          <span aria-hidden="true">₹</span>
          <input
            id="deposit-amount"
            type="number"
            min="1"
            step="0.01"
            inputMode="decimal"
            placeholder="Enter amount"
            value={amount}
            onChange={(event) => setAmount(event.target.value)}
            disabled={loading}
            required
          />
        </div>

        <div className="quick-deposits" aria-label="Quick deposit amounts">
          {QUICK_AMOUNTS.map((quickAmount) => (
            <button
              className={`quick-deposit${Number(amount) === quickAmount ? " selected" : ""}`}
              key={quickAmount}
              type="button"
              onClick={() => {
                setAmount(String(quickAmount));
                setError("");
                setSuccess("");
              }}
              disabled={loading}
            >
              {formatCurrency(quickAmount)}
            </button>
          ))}
        </div>

        {error && <div className="alert error" role="alert">{error}</div>}
        {success && <div className="alert success" role="status">{success}</div>}

        <button className="btn primary deposit-submit" type="submit" disabled={loading}>
          {loading ? "Adding demo balance…" : "Deposit to wallet"}
        </button>
        <p className="deposit-note">
          Demo balance only. This does not transfer real money.
        </p>
      </form>
    </section>
  );
}
