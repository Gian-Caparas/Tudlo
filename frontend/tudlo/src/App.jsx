import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'

function App() {
  const [count, setCount] = useState(0)

  return (
    <div className="min-h-screen bg-neutral-950 text-neutral-200 antialiased selection:bg-sky-500/30 selection:text-sky-300">
      {/* Hero Section */}
      <section className="flex flex-col items-center justify-center px-4 pt-20 pb-16 text-center">
        <div className="relative mb-8 flex h-48 w-48 items-center justify-center">
          <img src={heroImg} className="absolute h-40 w-40 animate-pulse opacity-40 blur-xl" alt="" />
          <img src={heroImg} className="relative z-10 h-40 w-40 object-contain" width="170" height="179" alt="" />
          <img src={reactLogo} className="absolute -bottom-2 -right-2 h-12 w-12 animate-[spin_20s_linear_infinite] drop-shadow-[0_0_15px_rgba(56,189,248,0.5)]" alt="React logo" />
          <img src={viteLogo} className="absolute -top-2 -left-2 h-12 w-12 drop-shadow-[0_0_15px_rgba(100,108,255,0.5)]" alt="Vite logo" />
        </div>
        
        <div className="max-w-md">
          <h1 className="bg-gradient-to-r from-white via-neutral-200 to-neutral-400 bg-clip-text text-5xl font-extrabold tracking-tight text-transparent sm:text-6xl">
            Get started
          </h1>
          <p className="mt-4 text-base text-neutral-400">
            Edit <code className="rounded bg-neutral-900 px-1.5 py-0.5 font-mono text-sm text-sky-400 border border-neutral-800">src/App.jsx</code> and save to test <code className="text-amber-400 font-semibold">HMR</code>
          </p>
        </div>

        <button
          type="button"
          className="group mt-8 inline-flex items-center gap-2 rounded-xl bg-white px-5 py-3 font-semibold text-neutral-950 shadow-lg shadow-white/5 transition-all hover:bg-neutral-200 active:scale-98"
          onClick={() => setCount((count) => count + 1)}
        >
          Count is 
          <span className="inline-flex h-6 min-w-6 items-center justify-center rounded-md bg-neutral-950 px-1.5 text-xs font-bold text-white transition-transform group-hover:scale-110">
            {count}
          </span>
        </button>
      </section>

      {/* Decorative Divider Line */}
      <div className="mx-auto max-w-5xl px-6">
        <div className="h-px bg-gradient-to-r from-transparent via-neutral-800 to-transparent"></div>
      </div>

      {/* Next Steps / Resources Grid */}
      <section className="mx-auto max-w-5xl px-6 py-16 grid gap-8 sm:grid-cols-2">
        {/* Documentation Card */}
        <div className="rounded-2xl border border-neutral-900 bg-neutral-900/30 p-6 backdrop-blur-sm">
          <div className="flex items-center gap-3">
            <svg className="h-6 w-6 text-sky-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" role="presentation" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <h2 className="text-xl font-bold text-white">Documentation</h2>
          </div>
          <p className="mt-2 text-sm text-neutral-400">Your questions, answered</p>
          <ul className="mt-6 space-y-3">
            <li>
              <a href="https://vite.dev/" target="_blank" rel="noreferrer" className="flex items-center gap-3 rounded-lg border border-neutral-900 bg-neutral-950 p-3 text-sm font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                <img className="h-5 w-5" src={viteLogo} alt="" />
                Explore Vite
              </a>
            </li>
            <li>
              <a href="https://react.dev/" target="_blank" rel="noreferrer" className="flex items-center gap-3 rounded-lg border border-neutral-900 bg-neutral-950 p-3 text-sm font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                <img className="h-5 w-5" src={reactLogo} alt="" />
                Learn more
              </a>
            </li>
          </ul>
        </div>

        {/* Community Card */}
        <div className="rounded-2xl border border-neutral-900 bg-neutral-900/30 p-6 backdrop-blur-sm">
          <div className="flex items-center gap-3">
            <svg className="h-6 w-6 text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" role="presentation" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <h2 className="text-xl font-bold text-white">Connect with us</h2>
          </div>
          <p className="mt-2 text-sm text-neutral-400">Join the Vite community</p>
          <ul className="mt-6 grid grid-cols-2 gap-3">
            <li>
              <a href="https://github.com/vitejs/vite" target="_blank" rel="noreferrer" className="flex items-center justify-center gap-2 rounded-lg border border-neutral-900 bg-neutral-950 py-2.5 text-xs font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                GitHub
              </a>
            </li>
            <li>
              <a href="https://chat.vite.dev/" target="_blank" rel="noreferrer" className="flex items-center justify-center gap-2 rounded-lg border border-neutral-900 bg-neutral-950 py-2.5 text-xs font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                Discord
              </a>
            </li>
            <li>
              <a href="https://x.com/vite_js" target="_blank" rel="noreferrer" className="flex items-center justify-center gap-2 rounded-lg border border-neutral-900 bg-neutral-950 py-2.5 text-xs font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                X.com
              </a>
            </li>
            <li>
              <a href="https://bsky.app/profile/vite.dev" target="_blank" rel="noreferrer" className="flex items-center justify-center gap-2 rounded-lg border border-neutral-900 bg-neutral-950 py-2.5 text-xs font-medium transition-colors hover:border-neutral-800 hover:bg-neutral-900">
                Bluesky
              </a>
            </li>
          </ul>
        </div>
      </section>
    </div>
  )
}

export default App
