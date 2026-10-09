export default function LoadingSpinner({ text = "Loading..." }) {
  return <div className="center-state"><div className="spinner" /><span>{text}</span></div>;
}
