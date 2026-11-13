import { Avatar } from "@/components/ui/Avatar.jsx";
import { Badge } from "@/components/ui/Badge.jsx";
import { Icon } from "@/components/ui/Icon.jsx";
export function Header({ role, page, userName, onLogout }) {
  return (
    <header>
      <div className="breadcrumb">
        <span>
          {role === "teacher" ? "CSIT321 – G6" : role === "student" ? "My learning" : "Tudlo Admin"}
        </span>
        <Icon name="chevron" size={13} />
        <b>{page}</b>
      </div>
      <div className="user-menu">
        <Avatar name={userName} size="sm" />
        <div>
          <strong>{userName}</strong>
          <span>{role.charAt(0).toUpperCase() + role.slice(1)}</span>
        </div>
        <Badge tone={role}>{role}</Badge>
        <button className="logout" onClick={onLogout} title="Log out">
          <Icon name="logout" />
        </button>
      </div>
    </header>
  );
}
