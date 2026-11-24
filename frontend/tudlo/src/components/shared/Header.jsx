import { Avatar } from "@/components/ui/Avatar.jsx";
import { Badge } from "@/components/ui/Badge.jsx";
import { Icon } from "@/components/ui/Icon.jsx";
export function Header({ role, page, userName, classLabel, onLogout, onMenu }) {
  const roleLabel = {
    admin: "Admin",
    teacher: "Teacher",
    student: "Student",
  }[role] ?? "User";

  return (
    <header>
      <div className="header-left">
        {onMenu && (
          <button type="button" className="menu-btn" onClick={onMenu} aria-label="Open menu">
            <span />
            <span />
            <span />
          </button>
        )}
        <div className="breadcrumb">
          <span>
            {role === "teacher"
              ? (classLabel || "Teacher Workspace")
              : role === "student"
                ? "My learning"
                : "Tudlo Admin"}
          </span>
          {role !== "student" && (
            <>
              <Icon name="chevron" size={13} />
              <b>{page}</b>
            </>
          )}
        </div>
      </div>
      <div className="user-menu">
        <Avatar name={userName} size="sm" />
        <div>
          <strong>{userName}</strong>
          <span>{roleLabel}</span>
        </div>
        <Badge tone={role}>{role}</Badge>
        <button className="logout" type="button" onClick={onLogout} title="Log out" aria-label="Log out">
          <Icon name="logout" />
        </button>
      </div>
    </header>
  );
}