import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { api } from '../lib/api'
import { Alert, Button, Card, Field, Input } from '../ui/primitives'

export function AdminPage() {
  const [policies, setPolicies] = useState<Array<Record<string, unknown>>>([])
  const [queue, setQueue] = useState<Array<Record<string, unknown>>>([])
  const [audit, setAudit] = useState<Array<Record<string, unknown>>>([])
  const [error, setError] = useState('')
  const [selected, setSelected] = useState<Record<string, unknown> | null>(null)
  const [value, setValue] = useState('')
  const [sourceUrl, setSourceUrl] = useState('')

  async function load() {
    try {
      const [p, q, a] = await Promise.all([
        api<Array<Record<string, unknown>>>('/api/v1/admin/policies'),
        api<Array<Record<string, unknown>>>('/api/v1/admin/stale-queue'),
        api<Array<Record<string, unknown>>>('/api/v1/admin/audit'),
      ])
      setPolicies(p)
      setQueue(q)
      setAudit(a)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Admin API unavailable')
    }
  }

  useEffect(() => { load() }, [])

  async function save(event: FormEvent) {
    event.preventDefault()
    if (!selected) return
    try {
      await api(`/api/v1/admin/policies/${selected.id}`, {
        method: 'PUT',
        body: JSON.stringify({
          institutionId: selected.institutionId,
          programId: selected.programId,
          catalogYear: selected.catalogYear,
          ruleType: selected.ruleType,
          ruleValue: value || selected.ruleValue,
          unit: selected.unit,
          sourceUrl: sourceUrl || selected.sourceUrl,
          lastVerifiedAt: new Date().toISOString().slice(0, 10),
          verifiedBy: 'admin-console',
          verificationStatus: 'VERIFIED',
          notes: selected.notes,
        }),
      })
      setSelected(null)
      await load()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Update failed')
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Admin console</p>
        <h2 className="serif text-3xl">Policy and equivalency data, not a workflow engine.</h2>
      </div>
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Card>
        <h3 className="serif text-xl">Stale-record review queue</h3>
        {queue.length === 0 ? <p className="mt-2 text-sm text-slate">No stale records at the current review intervals.</p> : (
          <ul className="mt-3 space-y-2 text-sm">
            {queue.map((item) => (
              <li key={String(item.id)} className="rounded-md border border-gold/30 bg-[#FBF4E4] px-3 py-2">
                {String(item.kind)} · {String(item.label)} · last verified {String(item.lastVerifiedAt)}
              </li>
            ))}
          </ul>
        )}
      </Card>
      <Card className="overflow-x-auto">
        <h3 className="serif text-xl">Policy rules</h3>
        <table className="mt-3 min-w-full text-left text-sm">
          <thead>
            <tr className="text-xs uppercase text-slate">
              <th className="pb-2">Type</th>
              <th className="pb-2">Value</th>
              <th className="pb-2">Verified</th>
              <th className="pb-2"></th>
            </tr>
          </thead>
          <tbody>
            {policies.slice(0, 40).map((rule) => (
              <tr key={String(rule.id)} className="border-t border-obsidian/5">
                <td className="py-2 pr-3">{String(rule.ruleType)}</td>
                <td className="py-2 pr-3">{String(rule.ruleValue)} {String(rule.unit)}</td>
                <td className="py-2 pr-3">{String(rule.lastVerifiedAt)}</td>
                <td className="py-2"><Button variant="ghost" onClick={() => { setSelected(rule); setValue(String(rule.ruleValue)); setSourceUrl(String(rule.sourceUrl || '')) }}>Edit</Button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </Card>
      {selected ? (
        <Card>
          <h3 className="serif text-xl">Update {String(selected.ruleType)}</h3>
          <form className="mt-3 grid gap-3 sm:grid-cols-2" onSubmit={save}>
            <Field label="Value"><Input value={value} onChange={(e) => setValue(e.target.value)} /></Field>
            <Field label="Source URL"><Input value={sourceUrl} onChange={(e) => setSourceUrl(e.target.value)} /></Field>
            <div className="sm:col-span-2 flex gap-2">
              <Button type="submit">Save and write audit event</Button>
              <Button type="button" variant="secondary" onClick={() => setSelected(null)}>Cancel</Button>
            </div>
          </form>
        </Card>
      ) : null}
      <Card>
        <h3 className="serif text-xl">Audit log</h3>
        <ul className="mt-3 space-y-2 text-sm">
          {audit.map((event) => (
            <li key={String(event.id)}>
              {String(event.createdAt)} · {String(event.action)} · {String(event.entityType)} · {String(event.afterValue || '')}
            </li>
          ))}
        </ul>
      </Card>
    </div>
  )
}
