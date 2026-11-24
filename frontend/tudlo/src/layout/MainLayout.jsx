import { useState } from "react";
import { Header } from "@/components/shared/Header.jsx";
import { Sidebar } from "@/components/shared/Sidebar.jsx";
export function MainLayout({
  role,
  page,
  userName,
  classLabel,
  studentCount,
  attentionCount,
  onNavigate,
  onLogout,
  children,
}) {
  const showSidebar = role === "teacher";
  const [menuOpen, setMenuOpen] = useState(false); // only matters on small screens

  // close the menu after picking a page
  function handleNavigate(label) {
    setMenuOpen(false);
    onNavigate(label);
  }

  return (
    <div
      className={`app-shell${showSidebar ? "" : " app-shell--no-sidebar"}${menuOpen ? " menu-open" : ""}`}
    >
      {showSidebar && (
        <Sidebar
          role={role}
          page={page}
          classLabel={classLabel}
          studentCount={studentCount}
          attentionCount={attentionCount}
          onNavigate={handleNavigate}
        />
      )}
      {showSidebar && (
        <div className="sidebar-backdrop" onClick={() => setMenuOpen(false)} />
      )}
      <main>
        <Header
          role={role}
          page={page}
          userName={userName}
          classLabel={classLabel}
          onLogout={onLogout}
          onMenu={showSidebar ? () => setMenuOpen(true) : undefined}
        />
        <div className="page">{children}</div>
      </main>
    </div>
  );
}