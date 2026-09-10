import type { FormEvent } from 'react'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'
import { Alert, Button, Field, Input } from '../ui/primitives'

function AuthFrame({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-ivory px-4 py-10">
      <div className="mx-auto max-w-md">
        <p className="text-[11px] uppercase tracking-[0.2em] text-gold-dark">The Degree Agency</p>
        <h1 className="serif mt-2 text-4xl text-obsidian">Degree Dean</h1>
        <p className="mt-3 text-sm text-slate">Don’t start over. Start strategically.</p>
        <div className="mt-8 rounded-xl border border-obsidian/10 bg-white p-6 shadow-sm">
          <h2 className="serif text-2xl">{title}</h2>
          <div className="mt-5 space-y-4">{children}</div>
        </div>
      </div>
    </div>
  )
}

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      const user = await login(email, password)
      navigate(user.role === 'ADMIN' ? '/admin' : '/onboarding')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to sign in')
    }
  }

  return (
    <AuthFrame title="Sign in">
      {error ? <Alert tone="error">{error}</Alert> : null}
      <form className="space-y-4" onSubmit={onSubmit}>
        <Field label="Email">
          <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </Field>
        <Field label="Password">
          <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </Field>
        <Button type="submit" className="w-full">Continue</Button>
      </form>
      <p className="text-sm text-slate">
        New here? <Link className="font-semibold text-gold-dark" to="/register">Create an account</Link>
      </p>
    </AuthFrame>
  )
}

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await register(email, password)
      navigate('/onboarding')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to create the account')
    }
  }

  return (
    <AuthFrame title="Create your account">
      <p className="text-sm text-slate">You probably have more educational leverage than you realize. Let’s figure out how to use it.</p>
      {error ? <Alert tone="error">{error}</Alert> : null}
      <form className="space-y-4" onSubmit={onSubmit}>
        <Field label="Email">
          <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </Field>
        <Field label="Password" hint="At least 10 characters. Hashed with Argon2id. We never store the original.">
          <Input type="password" minLength={10} value={password} onChange={(e) => setPassword(e.target.value)} required />
        </Field>
        <Button type="submit" className="w-full">Create account</Button>
      </form>
      <p className="text-sm text-slate">
        Already have an account? <Link className="font-semibold text-gold-dark" to="/login">Sign in</Link>
      </p>
    </AuthFrame>
  )
}
