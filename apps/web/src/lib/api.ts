const TOKEN_KEY = 'dd_access'
const REFRESH_KEY = 'dd_refresh'

export type AuthUser = {
  id: string
  email: string
  role: string
  tier: string
}

export function getAccessToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setSession(tokens: { accessToken: string; refreshToken: string }) {
  localStorage.setItem(TOKEN_KEY, tokens.accessToken)
  localStorage.setItem(REFRESH_KEY, tokens.refreshToken)
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_KEY)
}

async function refresh() {
  const refreshToken = localStorage.getItem(REFRESH_KEY)
  if (!refreshToken) throw new Error('Not signed in')
  const res = await fetch('/api/v1/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })
  if (!res.ok) {
    clearSession()
    throw new Error('Session expired')
  }
  const data = await res.json()
  setSession(data)
  return data.accessToken as string
}

export async function api<T>(path: string, init: RequestInit = {}, retry = true): Promise<T> {
  const headers = new Headers(init.headers)
  headers.set('Content-Type', 'application/json')
  const token = getAccessToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)
  const res = await fetch(path, { ...init, headers })
  if (res.status === 401 && retry && localStorage.getItem(REFRESH_KEY)) {
    const next = await refresh()
    headers.set('Authorization', `Bearer ${next}`)
    return api<T>(path, { ...init, headers }, false)
  }
  if (!res.ok) {
    let message = `Request failed (${res.status})`
    try {
      const body = await res.json()
      message = body.message || message
    } catch {
      /* ignore */
    }
    throw new Error(message)
  }
  if (res.status === 204) return undefined as T
  return res.json()
}
