import { useEffect, useId, useRef, useState } from "react";

// Smooth line chart with a soft area fill, y-axis numbers and an optional pass-mark line.
const HEIGHT = 260;
const PAD_LEFT = 44;   // room for the y-axis numbers
const PAD_RIGHT = 24;
const PAD_TOP = 30;
const PAD_BOTTOM = 42;
const CHAR_WIDTH = 6.5;  // about how wide one letter is at font size 11
const LABEL_GAP = 28;    // empty space between two neighbouring labels
const MIN_SLOT = 64;     // short names still get at least this much room
const DOT_COLOR = "#2f9e6e";  // the circles on the line
const PASS_COLOR = "#8a9894"; // the dashed pass-mark line

// "Quiz 1 - Variables" becomes "Quiz 1"
function shortLabel(text = "") {
  const first = String(text).split(" - ")[0];
  return first.length > 14 ? `${first.slice(0, 13)}…` : first;
}

export function LineChart({ values = [], labels = [], color = "#0f766e", threshold = null }) {
  const gradientId = `area-${useId().replace(/:/g, "")}`;
  const boxRef = useRef(null);
  const [boxWidth, setBoxWidth] = useState(0);

  // Measure the real width of the box so the text is never stretched
  useEffect(() => {
    const el = boxRef.current;
    if (!el) return;
    const update = () => setBoxWidth(el.clientWidth);
    update();
    const observer = new ResizeObserver(update);
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  if (values.length === 0) {
    return (
      <div className="line-chart">
        <div ref={boxRef} />
      </div>
    );
  }

  // 1. Each label gets a "slot" as wide as its name plus a gap
  const names = values.map((_, i) => shortLabel(labels[i]));
  const baseSlots = names.map((name) => Math.max(MIN_SLOT, name.length * CHAR_WIDTH + LABEL_GAP));
  const baseTotal = baseSlots.reduce((sum, w) => sum + w, 0);

  // 2. The chart is as wide as the box. On a phone it is wider than the box, so it scrolls sideways.
  const neededWidth = PAD_LEFT + PAD_RIGHT + baseTotal;
  const width = Math.max(boxWidth, neededWidth);

  // 3. If there is free space, share it equally so the gaps between labels stay the same
  const extra = (width - PAD_LEFT - PAD_RIGHT - baseTotal) / values.length;
  const slots = baseSlots.map((w) => w + extra);

  // 4. Each point sits in the middle of its slot
  const centers = [];
  let cursor = PAD_LEFT;
  slots.forEach((w) => {
    centers.push(cursor + w / 2);
    cursor += w;
  });

  // Round numbers on the y-axis: steps of 20 when scores are far apart, otherwise 10
  const spread = Math.max(...values) - Math.min(...values);
  const step = spread > 30 ? 20 : 10;
  const min = Math.max(0, Math.floor((Math.min(...values) - 10) / step) * step);
  const max = Math.min(100, Math.ceil((Math.max(...values) + 10) / step) * step);
  const ticks = [];
  for (let t = min; t <= max; t += step) ticks.push(t);

  const plotHeight = HEIGHT - PAD_TOP - PAD_BOTTOM;
  const bottom = PAD_TOP + plotHeight;

  const x = (i) => centers[i];
  const y = (v) => PAD_TOP + plotHeight - ((v - min) / (max - min || 1)) * plotHeight;

  // Curved line: each piece bends through the middle between two points
  const linePath = values
    .map((v, i) => {
      if (i === 0) return `M${x(i)},${y(v)}`;
      const mid = (x(i - 1) + x(i)) / 2;
      return `C${mid},${y(values[i - 1])} ${mid},${y(v)} ${x(i)},${y(v)}`;
    })
    .join(" ");
  const areaPath = `${linePath} L${x(values.length - 1)},${bottom} L${x(0)},${bottom} Z`;

  const showThreshold = threshold !== null && threshold >= min && threshold <= max;

  return (
    <div className="line-chart">
      <div ref={boxRef}>
        <svg
          viewBox={`0 0 ${width} ${HEIGHT}`}
          style={{ width, height: HEIGHT, display: "block" }}
          role="img"
          aria-label="Line chart of class average per assessment"
        >
          <defs>
            <linearGradient id={gradientId} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={color} stopOpacity="0.28" />
              <stop offset="100%" stopColor={color} stopOpacity="0" />
            </linearGradient>
          </defs>

          {/* grid lines + y-axis numbers */}
          {ticks.map((t) => (
            <g key={t}>
              <line x1={PAD_LEFT} y1={y(t)} x2={width - PAD_RIGHT} y2={y(t)} stroke="#e7eeec" strokeWidth="1" />
              <text x={PAD_LEFT - 10} y={y(t) + 4} textAnchor="end" fontSize="11" fill="#84928e">
                {t}%
              </text>
            </g>
          ))}

          {/* dashed pass-mark line */}
          {showThreshold && (
            <g>
              <line
                x1={PAD_LEFT}
                y1={y(threshold)}
                x2={width - PAD_RIGHT}
                y2={y(threshold)}
                stroke={PASS_COLOR}
                strokeWidth="1.5"
                strokeDasharray="5 5"
              />
              <text x={width - PAD_RIGHT} y={y(threshold) - 6} textAnchor="end" fontSize="10" fill={PASS_COLOR}>
                Pass mark {threshold}%
              </text>
            </g>
          )}

          <path d={areaPath} fill={`url(#${gradientId})`} />
          <path d={linePath} fill="none" stroke={color} strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />

          {values.map((v, i) => (
            <g key={i}>
              <title>{`${labels[i]}: ${v}%`}</title>
              <circle cx={x(i)} cy={y(v)} r="6" fill="white" stroke={DOT_COLOR} strokeWidth="3" />
              <text
                x={x(i)}
                y={y(v) - 14}
                textAnchor="middle"
                fontSize="12"
                fontWeight="600"
                fill="#2f403c"
                stroke="#fff"
                strokeWidth="3"
                paintOrder="stroke"
              >
                {v}%
              </text>
              <text x={x(i)} y={HEIGHT - 14} textAnchor="middle" fontSize="11" fill="#74837f">
                {names[i]}
              </text>
            </g>
          ))}
        </svg>
      </div>
    </div>
  );
}