// Shows a message while the data loads, when it fails, or when there is nothing yet.
// Once the data is ready, it shows what is inside it (the children).
export function DataGate({ data, children }) {
  if (data.status === 'error') {
    return (
      <section className="card state-card" role="alert">
        <h2>Could not load your class data</h2>
        <p>{data.error}</p>
        <button className="btn primary" type="button" onClick={data.retry}>Try again</button>
      </section>
    )
  }

  if (data.status === 'loading') {
    return (
      <section className="card state-card" role="status">
        <p>Loading your class data…</p>
      </section>
    )
  }

  if (data.subjects.length === 0) {
    return (
      <section className="card state-card">
        <h2>No subjects assigned yet</h2>
        <p>Ask an admin to assign a subject and section to your account.</p>
      </section>
    )
  }

  return children
}