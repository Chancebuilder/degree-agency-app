import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './lib/auth'
import { LoginPage, RegisterPage } from './pages/AuthPages'
import { OnboardingPage } from './pages/OnboardingPage'
import { GoalPage } from './pages/GoalPage'
import { ImportPage, WalletPage } from './pages/ImportAndWalletPages'
import { CostPage, DashboardPage, PlanPage, ScenarioPage, SimulatorPage } from './pages/PlanningPages'
import { PrivacyPage } from './pages/PrivacyPage'
import { AdminPage } from './pages/AdminPage'
import { AppShell } from './ui/AppShell'

function RequireAuth({ children }: { children: React.ReactNode }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="p-8 text-slate">Loading session…</div>
  if (!user) return <Navigate to="/login" replace />
  return children
}

function RequireAdmin({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  if (user?.role !== 'ADMIN') return <Navigate to="/dashboard" replace />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        element={
          <RequireAuth>
            <AppShell />
          </RequireAuth>
        }
      >
        <Route path="/onboarding" element={<OnboardingPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/wallet" element={<WalletPage />} />
        <Route path="/import" element={<ImportPage />} />
        <Route path="/goal" element={<GoalPage />} />
        <Route path="/plan" element={<PlanPage />} />
        <Route path="/simulator" element={<SimulatorPage />} />
        <Route path="/scenarios" element={<ScenarioPage />} />
        <Route path="/cost" element={<CostPage />} />
        <Route path="/privacy" element={<PrivacyPage />} />
        <Route
          path="/admin"
          element={
            <RequireAdmin>
              <AdminPage />
            </RequireAdmin>
          }
        />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
