export function TopicBars({
  values = [92, 48, 69, 74, 56],
  labels = ["UI Design", "Programming Basics", "Databases", "Web Development", "Algorithms"],
  compact = false,
}) {
  return (
    <div className={`bars ${compact ? "compact" : ""}`}>
      {values.map((v, i) => (
        <div className="bar-row" key={labels[i]}>
          <div className="bar-label">
            <span>{labels[i]}</span>
            <strong>{v}%</strong>
          </div>
          <div className="bar-track">
            <div
              className={`bar-fill ${v < 60 ? "weak" : v >= 85 ? "strong" : ""}`}
              style={{ width: `${v}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}
