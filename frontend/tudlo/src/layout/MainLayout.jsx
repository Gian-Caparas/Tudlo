import { Header } from "@/components/shared/Header.jsx";
import { Sidebar } from "@/components/shared/Sidebar.jsx";
export function MainLayout({ role, page, userName, onNavigate, onLogout, children }) {
  return (
    <div className="app-shell">
      <Sidebar role={role} page={page} onNavigate={onNavigate} />
      <main>
        <Header role={role} page={page} userName={userName} onLogout={onLogout} />
        <div className="page">{children}</div>
      </main>
    </div>
  );
}
