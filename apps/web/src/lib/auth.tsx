import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { api, clearSession, setSession, type AuthUser } from './api'

type AuthContextValue = {
  user: AuthUser | null
  loading: boolean
  login: (email: string, password: string) => Promise<AuthUser>
  register: (email: string, password: string) => Promise<AuthUser>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api<AuthUser>('/api/v1/auth/me')
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
  }, [])

  const value = useMemo<AuthContextValue>(() => ({
    user,
    loading,
    async login(email, password) {
      const tokens = await api<AuthUser & { accessToken: string; refreshToken: string }>('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      })
      setSession(tokens)
      const me = { id: tokens.id, email: tokens.email, role: tokens.role, tier: tokens.tier }
      setUser(me)
      return me
    },
    async register(email, password) {
      const tokens = await api<AuthUser & { accessToken: string; refreshToken: string }>('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      })
      setSession(tokens)
      const me = { id: tokens.id, email: tokens.email, role: tokens.role, tier: tokens.tier }
      setUser(me)
      return me
    },
    async logout() {
      const refreshToken = localStorage.getItem('dd_refresh')
      try {
        await api('/api/v1/auth/logout', { method: 'POST', body: JSON.stringify({ refreshToken }) })
      } finally {
        clearSession()
        setUser(null)
      }
    },
  }), [user, loading])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
