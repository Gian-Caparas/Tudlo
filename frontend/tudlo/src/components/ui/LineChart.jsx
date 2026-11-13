export function LineChart({
  values = [68, 72, 70, 78],
  labels = ["Quiz 1", "Quiz 2", "Midterm", "Milestone 1"],
  color = "#0f766e",
}) {
  const min = 35,
    max = 100;
  const points = values
    .map(
      (v, i) => `${10 + i * (280 / (values.length - 1))},${130 - ((v - min) / (max - min)) * 100}`,
    )
    .join(" ");
  return (
    <div className="line-chart">
      <svg viewBox="0 0 310 150" preserveAspectRatio="none">
        {[30, 80, 130].map((y) => (
          <line key={y} x1="8" y1={y} x2="300" y2={y} stroke="#e7eeec" strokeWidth="1" />
        ))}
        <polyline
          points={points}
          fill="none"
          stroke={color}
          strokeWidth="3"
          strokeLinejoin="round"
          strokeLinecap="round"
        />
        {values.map((v, i) => (
          <g key={i}>
            <circle
              cx={10 + i * (280 / (values.length - 1))}
              cy={130 - ((v - min) / (max - min)) * 100}
              r="4"
              fill="white"
              stroke={color}
              strokeWidth="3"
            />
            <text
              x={10 + i * (280 / (values.length - 1))}
              y={122 - ((v - min) / (max - min)) * 100}
              textAnchor="middle"
              fontSize="9"
              fill="#435551"
            >
              {v}%
            </text>
          </g>
        ))}
      </svg>
      <div className="chart-labels">
        {labels.map((l) => (
          <span key={l}>{l}</span>
        ))}
      </div>
    </div>
  );
}
