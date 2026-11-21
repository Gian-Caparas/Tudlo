// Draws a line chart. The size stays in proportion, so circles stay round and text is not stretched.
const WIDTH = 560
const HEIGHT = 230
const PAD_X = 50
const PAD_TOP = 34
const PAD_BOTTOM = 36

// Long names like "Quiz 1 - Variables" are shortened to "Quiz 1" so the labels do not overlap
export function LineChart({ values = [], labels = [], color = "#0f766e" }) {
  if (values.length === 0) return null

  // The vertical scale follows the data, in steps of 10
  const min = Math.max(0, Math.floor((Math.min(...values) - 10) / 10) * 10);
  const max = Math.min(100, Math.ceil((Math.max(...values) + 10) / 10) * 10);
  const plotHeight = HEIGHT - PAD_TOP - PAD_BOTTOM;

  const x = (i) =>
    values.length === 1 ? WIDTH / 2 : PAD_X + (i * (WIDTH - PAD_X * 2)) / (values.length - 1);
  const y = (v) => PAD_TOP + plotHeight - ((v - min) / (max - min || 1)) * plotHeight;

  const points = values.map((v, i) => `${x(i)},${y(v)}`).join(" ");
  const gridLines = [0, 0.5, 1].map((t) => PAD_TOP + plotHeight * t);

  return (
    <div className="line-chart">
      <svg
        viewBox={`0 0 ${WIDTH} ${HEIGHT}`}
        style={{ width: "100%", height: "auto", display: "block" }}
        role="img"
        aria-label="Line chart"
      >
        {gridLines.map((gy) => (
          <line key={gy} x1={16} y1={gy} x2={WIDTH - 16} y2={gy} stroke="#e7eeec" strokeWidth="1" />
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
            <circle cx={x(i)} cy={y(v)} r="5" fill="white" stroke={color} strokeWidth="3" />
            <text x={x(i)} y={y(v) - 13} textAnchor="middle" fontSize="12" fontWeight="600" fill="#435551">
              {v}%
            </text>
            <text x={x(i)} y={HEIGHT - 10} textAnchor="middle" fontSize="11" fill="#84928e">
              {labels[i]}
            </text>
          </g>
        ))}
      </svg>
    </div>
  );
}