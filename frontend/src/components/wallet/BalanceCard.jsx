import { formatCurrency } from "../../utils/formatCurrency";

export default function BalanceCard({ user }) {
  return (
    <div className="balance-card">
      <small>Total balance</small>
      <strong>{formatCurrency(user?.balance)}</strong>
      <div className="balance-details">
        <span>Reserved <b>{formatCurrency(user?.reservedBalance)}</b></span>
        <span>Available <b>{formatCurrency(user?.availableBalance)}</b></span>
      </div>
    </div>
  );
}
