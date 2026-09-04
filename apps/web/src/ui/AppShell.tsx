import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'
import { Button } from './primitives'

const studentLinks = [
  ['/dashboard', 'Dashboard'],
  ['/wallet', 'Academic Wallet'],
  ['/import', 'Import'],
  ['/goal', 'Degree goal'],
  ['/plan', 'Degree plan'],
  ['/simulator', 'Simulator'],
  ['/scenarios', 'Scenarios'],
  ['/cost', 'Cost'],
  ['/privacy', 'Privacy'],
]

export function AppShell() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const links = user?.role === 'ADMIN'
    ? [...studentLinks, ['/admin', 'Admin']]
    : studentLinks

  return (
    <div className="min-h-screen bg-ivory">
      <header className="border-b border-obsidian/10 bg-obsidian text-ivory">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-4">
          <div>
            <p className="text-[11px] uppercase tracking-[0.2em] text-gold">The Degree Agency</p>
            <h1 className="serif text-xl text-white">Degree Dean</h1>
          </div>
          <div className="flex items-center gap-3 text-sm">
            <span className="hidden text-ivory/70 sm:inline">{user?.email}</span>
            <span className="rounded-full border border-gold/50 px-2 py-0.5 text-[11px] uppercase tracking-wide text-gold">{user?.tier}</span>
            <Button
              variant="secondary"
              className="border-white/20 bg-transparent text-ivory hover:border-gold"
              onClick={async () => {
                await logout()
                navigate('/login')
              }}
            >
              Sign out
            </Button>
          </div>
        </div>
      </header>
      <div className="mx-auto flex max-w-6xl flex-col gap-6 px-4 py-6 md:flex-row">
        <nav className="flex gap-2 overflow-x-auto md:w-52 md:flex-col md:overflow-visible">
          {links.map(([to, label]) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `whitespace-nowrap rounded-md px-3 py-2 text-sm ${isActive ? 'bg-white font-semibold text-obsidian shadow-sm' : 'text-slate hover:bg-white/70 hover:text-charcoal'}`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
        <main className="min-w-0 flex-1 space-y-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
