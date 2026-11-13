export function Avatar({ name, size = "md" }) {
  const initials = name
    .replace("Prof. ", "")
    .split(" ")
    .map((n) => n[0])
    .slice(0, 2)
    .join("");
  return <span className={`avatar avatar-${size}`}>{initials}</span>;
}
