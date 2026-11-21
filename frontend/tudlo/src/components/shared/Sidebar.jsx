import { Icon } from "@/components/ui/Icon.jsx";
import { navigationByRole } from "@/routes";
export function Sidebar({
  role,
  page,
  classLabel,
  studentCount = 0,
  attentionCount = 0,
  onNavigate,
}) {
  const teacherClass = classLabel || "No class selected";
  const teacherStudentText = classLabel
    ? `${studentCount} ${studentCount === 1 ? "student" : "students"}`
    : "Import a class list to begin";
  const teacherAttentionTitle =
    attentionCount > 0
      ? `${attentionCount} ${attentionCount === 1 ? "learner needs" : "learners need"} attention`
      : "All learners on track";
  const teacherAttentionSub =
    attentionCount > 0 ? "Overall grade below 60%" : "No students below 60%";

  return (
    <aside>
      <div className="logo">
        <span>T</span> Tudlo
      </div>
      <div className="context">
        <span>
          {role === "teacher" ? "TEACHING" : role === "student" ? "LEARNING" : "WORKSPACE"}
        </span>
        <strong>{role === "admin" ? "Administration" : teacherClass}</strong>
        {role === "teacher" && <small>{teacherStudentText}</small>}
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
            ? teacherAttentionTitle
            : role === "student"
              ? "Keep your momentum"
              : "All systems operational"}
        </strong>
        <span>
          {role === "teacher"
            ? teacherAttentionSub
            : role === "student"
              ? "Your latest score improved"
              : "Last checked today"}
        </span>
      </div>
    </aside>
  );
}
