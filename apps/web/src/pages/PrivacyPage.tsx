import { useEffect, useState } from 'react'
import { api } from '../lib/api'
import { Alert, Button, Card } from '../ui/primitives'

type Consent = {
  id: string
  purpose: string
  granted: boolean
  policyVersion: string
  createdAt: string
  withdrawnAt?: string
  acknowledgmentText?: string
}

export function PrivacyPage() {
  const [items, setItems] = useState<Consent[]>([])
  const [error, setError] = useState('')
  const [exportText, setExportText] = useState('')

  async function load() {
    try {
      setItems(await api<Consent[]>('/api/v1/consents'))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to load consent history')
    }
  }

  useEffect(() => { load() }, [])

  async function withdraw(purpose: string) {
    setError('')
    try {
      await api(`/api/v1/consents/${purpose}/withdraw`, { method: 'POST' })
      await load()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to withdraw consent')
    }
  }

  async function exportData() {
    const [profile, wallet, consents] = await Promise.all([
      api('/api/v1/profile'),
      api('/api/v1/wallet'),
      api('/api/v1/consents'),
    ])
    setExportText(JSON.stringify({ profile, wallet, consents }, null, 2))
  }

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Privacy</p>
        <h2 className="serif text-3xl">Purpose-limited consent, with a history.</h2>
      </div>
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Card>
        <h3 className="serif text-xl">Consent history</h3>
        <ul className="mt-3 space-y-3 text-sm">
          {items.map((item) => (
            <li key={item.id} className="rounded-lg border border-obsidian/10 p-3">
              <p className="font-semibold">{item.purpose} · {item.granted ? 'Granted' : 'Withdrawn or denied'}</p>
              <p className="text-slate">Version {item.policyVersion} · {new Date(item.createdAt).toLocaleString()}</p>
              {item.acknowledgmentText ? <p className="mt-1 text-slate">{item.acknowledgmentText}</p> : null}
              {item.granted && !['DEGREE_PLANNING', 'ACCOUNT_STORAGE', 'DISCLOSURE_ACKNOWLEDGMENT'].includes(item.purpose) ? (
                <Button className="mt-2" variant="secondary" onClick={() => withdraw(item.purpose)}>Withdraw</Button>
              ) : null}
            </li>
          ))}
        </ul>
      </Card>
      <Card>
        <h3 className="serif text-xl">Export your structured data</h3>
        <p className="mt-2 text-sm text-slate">This exports profile, wallet, and consent records. Original transcripts were never uploaded.</p>
        <Button className="mt-3" onClick={exportData}>Export JSON</Button>
        {exportText ? <pre className="mt-4 max-h-80 overflow-auto rounded-md bg-obsidian p-3 text-xs text-ivory">{exportText}</pre> : null}
      </Card>
    </div>
  )
}
