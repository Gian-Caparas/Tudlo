import { Icon } from "@/components/ui/Icon.jsx";
import { navigationByRole } from "@/routes";
export function Sidebar({ role, page, onNavigate }) {
  return (
    <aside>
      <div className="logo">
        <span>T</span> Tudlo
      </div>
      <div className="context">
        <span>
          {role === "teacher" ? "TEACHING" : role === "student" ? "LEARNING" : "WORKSPACE"}
        </span>
        <strong>{role === "admin" ? "Administration" : "CSIT321 – G6"}</strong>
        {role === "teacher" && <small>42 students</small>}
      </div>
      <nav>
        {navigationByRole[role].map((item) => (
          <button
            className={page === item.label ? "active" : ""}
            key={item.label}
            onClick={() => onNavigate(item.label)}
          >
            <Icon name={item.icon} />
            <span>{item.label}</span>
            {page === item.label && <i />}
          </button>
        ))}
      </nav>
      <div className="side-insight">
        <Icon name={role === "teacher" ? "heart" : role === "student" ? "chart" : "settings"} />
        <strong>
          {role === "teacher"
            ? "4 learners need attention"
            : role === "student"
              ? "Keep your momentum"
              : "All systems operational"}
        </strong>
        <span>
          {role === "teacher"
            ? "Review recent flags"
            : role === "student"
              ? "Your latest score improved"
              : "Last checked today"}
        </span>
      </div>
    </aside>
  );
}
