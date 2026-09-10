import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'
import { Alert, Button, Card, EstimateNote, Field, Input } from '../ui/primitives'

type Dashboard = {
  ready: boolean
  message?: string
  profile?: Record<string, unknown>
  institution?: { name: string; shortName: string }
  program?: { name: string; catalogYear: string; totalCredits: number; lastVerifiedAt: string; sourceUrl: string }
  plan?: { id: string }
  match?: Record<string, unknown>
  scenarios?: Array<Record<string, unknown>>
  nextBestActions?: Array<Record<string, unknown>>
  estimateQualifier?: string
}

function useDashboard() {
  const [data, setData] = useState<Dashboard | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    api<Dashboard>('/api/v1/dashboard')
      .then(setData)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])
  return { data, error, loading, setData, setError }
}

export function DashboardPage() {
  const { data, error, loading } = useDashboard()
  if (loading) return <Card>Loading your plan…</Card>
  if (error) return <Alert tone="error">{error}</Alert>
  if (!data?.ready) {
    return (
      <Card>
        <h2 className="serif text-2xl">Your dashboard is waiting on a goal.</h2>
        <p className="mt-2 text-sm text-slate">{data?.message}</p>
        <Link className="mt-4 inline-block text-sm font-semibold text-gold-dark" to="/goal">Choose a degree goal</Link>
      </Card>
    )
  }
  const match = data.match || {}
  const confirmed = Number(match.confirmedCredits || 0)
  const needs = Number(match.needsConfirmationCredits || 0)
  const remaining = Number(match.remainingCredits || 0)
  const progress = Number(match.progressPercent || 0)
  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Student dashboard</p>
        <h2 className="serif text-3xl">{data.program?.name}</h2>
        <p className="text-sm text-slate">{data.institution?.name} · catalog {data.program?.catalogYear}</p>
      </div>
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <p className="text-xs uppercase text-slate">Degree countdown</p>
          <p className="serif mt-2 text-4xl">{progress}%</p>
          <p className="text-sm text-slate">{remaining} credits remaining</p>
        </Card>
        <Card>
          <p className="text-xs uppercase text-slate">Confirmed credits</p>
          <p className="serif mt-2 text-4xl text-evergreen">{confirmed}</p>
          <p className="text-sm text-slate">Needs confirmation: {needs}</p>
        </Card>
        <Card>
          <p className="text-xs uppercase text-slate">Next best action</p>
          <p className="mt-2 text-sm">{String(data.nextBestActions?.[0]?.rationale || 'Run the simulator to generate a next action.')}</p>
        </Card>
        <Card>
          <p className="text-xs uppercase text-slate">Verification</p>
          <p className="mt-2 text-sm">Requirements verified {data.program?.lastVerifiedAt}</p>
          <a className="text-sm text-gold-dark" href={data.program?.sourceUrl} target="_blank" rel="noreferrer">Source</a>
        </Card>
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        {['FASTEST', 'CHEAPEST', 'BEST_FIT'].map((type) => {
          const scenario = data.scenarios?.find((s) => s.scenarioType === type)
          return (
            <Card key={type}>
              <p className="text-xs uppercase text-slate">{type.replaceAll('_', ' ')}</p>
              {scenario ? (
                <>
                  <p className="mt-2 text-lg font-semibold">{String(scenario.projectedCompletionDate || '—')}</p>
                  <p className="text-sm text-slate">${Number(scenario.totalCost || 0).toLocaleString()} · {String(scenario.feasibility || '')}</p>
                  {scenario.staleBlocked ? <p className="mt-2 text-sm text-burgundy">Optimization claim withheld. {String(scenario.blockedReason || '')}</p> : null}
                </>
              ) : (
                <Link className="mt-3 inline-block text-sm font-semibold text-gold-dark" to="/simulator">Run simulator</Link>
              )}
            </Card>
          )
        })}
      </div>
      <EstimateNote>{data.estimateQualifier}</EstimateNote>
    </div>
  )
}

export function PlanPage() {
  const { data, error, setError } = useDashboard()
  const [detail, setDetail] = useState<Record<string, unknown> | null>(null)

  async function runMatch() {
    if (!data?.plan?.id) return
    try {
      const result = await api<Record<string, unknown>>(`/api/v1/plans/${data.plan.id}/match`, { method: 'POST' })
      setDetail(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Match failed')
    }
  }

  useEffect(() => {
    if (data?.plan?.id) {
      api<Record<string, unknown>>(`/api/v1/plans/${data.plan.id}/match`).then(setDetail).catch(() => undefined)
    }
  }, [data?.plan?.id])

  if (error) return <Alert tone="error">{error}</Alert>
  if (!data?.ready) return <Card>Create a degree goal first.</Card>

  const applied = (detail?.applied as Array<Record<string, unknown>>) || []
  const surplus = (detail?.surplus as Array<Record<string, unknown>>) || []
  const unapplied = (detail?.unapplied as Array<Record<string, unknown>>) || []
  const remaining = (detail?.remainingSlots as Array<Record<string, unknown>>) || []

  return (
    <div className="space-y-4">
      <div className="flex items-end justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Degree plan</p>
          <h2 className="serif text-3xl">What applies, what is surplus, and why.</h2>
        </div>
        <Button onClick={runMatch}>Run matching pipeline</Button>
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        <Card><p className="text-xs uppercase text-slate">Applied</p><p className="serif text-3xl">{String(detail?.appliedCredits ?? '—')}</p></Card>
        <Card><p className="text-xs uppercase text-slate">Confirmed / needs confirmation</p><p className="serif text-3xl">{String(detail?.confirmedCredits ?? 0)} / {String(detail?.needsConfirmationCredits ?? 0)}</p></Card>
        <Card><p className="text-xs uppercase text-slate">Remaining</p><p className="serif text-3xl">{String(detail?.remainingCredits ?? '—')}</p></Card>
      </div>
      <MatchTable title="Applied credits" rows={applied} />
      <MatchTable title="Surplus credits" rows={surplus} empty="No surplus. That is uncommon and worth celebrating only after you check the cap." />
      <MatchTable title="Unapplied assets" rows={unapplied} empty="Every reviewed asset matched a requirement slot." />
      <Card>
        <h3 className="serif text-xl">Remaining requirement slots</h3>
        <ul className="mt-3 space-y-1 text-sm">
          {remaining.map((slot) => (
            <li key={String(slot.id)}>{String(slot.slotCode)} · {String(slot.title)} · {String(slot.credits)} cr {slot.residencyRequired ? '· residency' : ''}</li>
          ))}
        </ul>
      </Card>
      <EstimateNote />
    </div>
  )
}

function MatchTable({ title, rows, empty }: { title: string; rows: Array<Record<string, unknown>>; empty?: string }) {
  return (
    <Card>
      <h3 className="serif text-xl">{title}</h3>
      {rows.length === 0 ? <p className="mt-2 text-sm text-slate">{empty || 'None yet. Run matching after the wallet is saved.'}</p> : (
        <ul className="mt-3 space-y-3 text-sm">
          {rows.map((row, index) => (
            <li key={index} className="rounded-lg border border-obsidian/10 p-3">
              <p className="font-semibold">{String((row.asset as Record<string, unknown> | undefined)?.courseTitle || (row.classification ?? 'Asset'))}</p>
              <p className="text-slate">{String(row.classification)} · {String(row.confidence)} · {String(row.reason || '')}</p>
              {row.slot ? <p>Slot: {String((row.slot as Record<string, unknown>).title)}</p> : null}
              {row.equivalency ? <p>Equivalency source: {String((row.equivalency as Record<string, unknown>).sourceUrl || '')}</p> : null}
            </li>
          ))}
        </ul>
      )}
    </Card>
  )
}

export function SimulatorPage() {
  const { data, error, setError } = useDashboard()
  const [hours, setHours] = useState(12)
  const [budget, setBudget] = useState(400)
  const [target, setTarget] = useState('2028-05-01')
  const [result, setResult] = useState<Record<string, unknown> | null>(null)

  useEffect(() => {
    if (data?.profile) {
      setHours(Number(data.profile.weeklyStudyHours || 12))
      setBudget(Number(data.profile.monthlyBudget || 400))
      if (data.profile.targetGraduationDate) setTarget(String(data.profile.targetGraduationDate))
    }
  }, [data])

  async function run(event: FormEvent) {
    event.preventDefault()
    if (!data?.plan?.id) return
    try {
      await api('/api/v1/profile', {
        method: 'PUT',
        body: JSON.stringify({
          firstName: data.profile?.firstName || 'Student',
          lastName: data.profile?.lastName || 'Learner',
          weeklyStudyHours: hours,
          monthlyBudget: budget,
          intensity: data.profile?.intensity || 'STANDARD',
          maxConcurrent: data.profile?.maxConcurrent || 2,
          competencyWilling: true,
          examWilling: true,
          targetGraduationDate: target,
          startDate: data.profile?.startDate || new Date().toISOString().slice(0, 10),
        }),
      })
      const simulated = await api<Record<string, unknown>>(`/api/v1/plans/${data.plan.id}/simulate`, { method: 'POST' })
      setResult(simulated)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Simulation failed')
    }
  }

  if (error) return <Alert tone="error">{error}</Alert>
  if (!data?.ready) return <Card>Create a goal and import a wallet first.</Card>
  const scenarios = (result?.scenarios || {}) as Record<string, { scenario: Record<string, unknown>; asOfNote: string }>

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Completion simulator</p>
        <h2 className="serif text-3xl">Adjust the time you actually have.</h2>
      </div>
      <Card>
        <form className="grid gap-4 sm:grid-cols-3" onSubmit={run}>
          <Field label="Weekly study hours"><Input type="number" min={1} max={60} value={hours} onChange={(e) => setHours(Number(e.target.value))} /></Field>
          <Field label="Monthly budget"><Input type="number" min={0} value={budget} onChange={(e) => setBudget(Number(e.target.value))} /></Field>
          <Field label="Target graduation"><Input type="date" value={target} onChange={(e) => setTarget(e.target.value)} /></Field>
          <div className="sm:col-span-3"><Button type="submit">Simulate Fastest, Cheapest, and Best Fit</Button></div>
        </form>
      </Card>
      {result?.blockedOptimization ? <Alert tone="warn">{String(result.blockingReason)}</Alert> : null}
      <div className="grid gap-4 md:grid-cols-3">
        {Object.entries(scenarios).filter(([key]) => ['FASTEST', 'CHEAPEST', 'BEST_FIT'].includes(key)).map(([key, value]) => (
          <Card key={key}>
            <p className="text-xs uppercase text-slate">{key.replaceAll('_', ' ')}</p>
            <p className="serif mt-2 text-2xl">{String(value.scenario.projectedCompletionDate)}</p>
            <p className="text-sm">${Number(value.scenario.totalCost || 0).toLocaleString()}</p>
            <p className="text-sm text-slate">{String(value.scenario.feasibility)} · {String(value.scenario.weeksRemaining)} weeks</p>
            <p className="mt-2 text-xs text-slate">{value.asOfNote}</p>
          </Card>
        ))}
      </div>
      <Card>
        <h3 className="serif text-xl">Next Best Action</h3>
        <ol className="mt-3 list-decimal space-y-2 pl-5 text-sm">
          {((result?.nextBestActions as Array<Record<string, unknown>>) || []).map((item) => (
            <li key={String(item.id)}>{String(item.rationale)}</li>
          ))}
        </ol>
      </Card>
      <EstimateNote>{String(result?.estimateQualifier || '')}</EstimateNote>
    </div>
  )
}

export function ScenarioPage() {
  const { data } = useDashboard()
  const [payload, setPayload] = useState<Record<string, unknown> | null>(null)
  useEffect(() => {
    if (data?.plan?.id) {
      api<Record<string, unknown>>(`/api/v1/plans/${data.plan.id}/scenarios`).then(setPayload).catch(() => undefined)
    }
  }, [data?.plan?.id])
  const scenarios = (payload?.scenarios || {}) as Record<string, { scenario: Record<string, unknown>; costItems: Array<Record<string, unknown>>; asOfNote: string }>
  return (
    <div className="space-y-4">
      <h2 className="serif text-3xl">Scenario comparison</h2>
      {Object.keys(scenarios).length === 0 ? <Card>Run the simulator to compare Fastest, Cheapest, and Best Fit.</Card> : (
        <div className="grid gap-4 md:grid-cols-2">
          {Object.entries(scenarios).map(([key, value]) => (
            <Card key={key}>
              <h3 className="serif text-xl">{key.replaceAll('_', ' ')}</h3>
              <p className="mt-2 text-sm">{String(value.scenario.projectedCompletionDate)} · ${Number(value.scenario.totalCost || 0).toLocaleString()}</p>
              <p className="text-sm text-slate">{String(value.scenario.feasibility)} · confidence {String(value.scenario.confidence)}</p>
              <p className="mt-2 text-xs text-slate">{value.asOfNote}</p>
            </Card>
          ))}
        </div>
      )}
      <EstimateNote />
    </div>
  )
}

export function CostPage() {
  const { data } = useDashboard()
  const [payload, setPayload] = useState<Record<string, unknown> | null>(null)
  useEffect(() => {
    if (data?.plan?.id) {
      api<Record<string, unknown>>(`/api/v1/plans/${data.plan.id}/scenarios`).then(setPayload).catch(() => undefined)
    }
  }, [data?.plan?.id])
  const scenarios = (payload?.scenarios || {}) as Record<string, { scenario: Record<string, unknown>; costItems: Array<Record<string, unknown>> }>
  const best = scenarios.BEST_FIT || scenarios.CUSTOM
  return (
    <div className="space-y-4">
      <h2 className="serif text-3xl">Remaining cost to degree</h2>
      {!best ? <Card>Run the simulator to itemize remaining cost.</Card> : (
        <Card>
          <p className="serif text-4xl">${Number(best.scenario.totalCost || 0).toLocaleString()}</p>
          <p className="text-sm text-slate">Best Fit total. Every line includes the as-of date of the underlying record.</p>
          <ul className="mt-4 space-y-2 text-sm">
            {(best.costItems || []).map((item) => (
              <li key={String(item.id)} className="flex justify-between gap-4 border-b border-obsidian/5 py-2">
                <span>{String(item.label)} <span className="text-slate">as of {String(item.asOf || 'n/a')}</span></span>
                <span>${Number(item.amount || 0).toLocaleString()}</span>
              </li>
            ))}
          </ul>
        </Card>
      )}
      <EstimateNote />
    </div>
  )
}
