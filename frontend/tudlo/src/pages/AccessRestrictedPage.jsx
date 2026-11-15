export function AccessRestrictedPage({ onNavigateHome }) {
  return (
    <section role="alert" className="rounded-xl border border-amber-200 bg-amber-50 p-6 text-amber-950">
      <h1 className="text-xl font-semibold">Access restricted</h1>
      <p className="mt-2">Your account is not allowed to open this page.</p>
      <button className="btn primary mt-4" type="button" onClick={onNavigateHome}>
        Return to your home page
      </button>
    </section>
  )
}
