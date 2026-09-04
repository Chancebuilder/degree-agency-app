import type { ButtonHTMLAttributes, InputHTMLAttributes, ReactNode } from 'react'

export function Button({
  children,
  variant = 'primary',
  className = '',
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'primary' | 'secondary' | 'ghost' | 'danger' }) {
  const styles = {
    primary: 'bg-obsidian text-ivory hover:bg-charcoal',
    secondary: 'border border-obsidian/15 bg-white text-charcoal hover:border-gold',
    ghost: 'text-charcoal hover:bg-white/70',
    danger: 'bg-burgundy text-white hover:opacity-90',
  }[variant]
  return (
    <button
      className={`inline-flex items-center justify-center rounded-md px-4 py-2.5 text-sm font-semibold transition disabled:opacity-50 ${styles} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}

export function Input({ className = '', ...props }: InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      className={`w-full rounded-md border border-obsidian/15 bg-white px-3 py-2.5 text-sm text-charcoal outline-none focus:border-gold ${className}`}
      {...props}
    />
  )
}

export function Field({ label, hint, children }: { label: string; hint?: string; children: ReactNode }) {
  return (
    <label className="block space-y-1.5">
      <span className="text-sm font-semibold text-charcoal">{label}</span>
      {children}
      {hint ? <span className="block text-xs text-slate">{hint}</span> : null}
    </label>
  )
}

export function Card({ children, className = '' }: { children: ReactNode; className?: string }) {
  return <section className={`rounded-xl border border-obsidian/10 bg-white p-5 shadow-sm ${className}`}>{children}</section>
}

export function Alert({ children, tone = 'info' }: { children: ReactNode; tone?: 'info' | 'warn' | 'error' | 'success' }) {
  const styles = {
    info: 'border-obsidian/10 bg-ivory text-charcoal',
    warn: 'border-gold/40 bg-[#FBF4E4] text-charcoal',
    error: 'border-burgundy/30 bg-[#F8ECEE] text-burgundy',
    success: 'border-evergreen/30 bg-[#EAF3EE] text-evergreen',
  }[tone]
  return <div className={`rounded-lg border px-4 py-3 text-sm ${styles}`}>{children}</div>
}

export function EstimateNote({ children }: { children?: ReactNode }) {
  return (
    <p className="text-xs leading-relaxed text-slate">
      {children || 'Estimate only. The Degree Agency is not a college, accreditor, or registrar. Final authority rests with the receiving institution.'}
    </p>
  )
}
