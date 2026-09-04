import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../lib/api'
import { parseTranscript, type CourseCandidate } from '../lib/transcriptParser'
import { Alert, Button, Card, EstimateNote, Input } from '../ui/primitives'

export function ImportPage() {
  const navigate = useNavigate()
  const [candidates, setCandidates] = useState<CourseCandidate[]>([])
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function onFile(file: File) {
    setError('')
    if (file.name.toLowerCase().endsWith('.pdf')) {
      setError('PDF and OCR parsing is Phase 2. Export CSV, TSV, or TXT and import that file.')
      return
    }
    const text = await file.text()
    try {
      setCandidates(parseTranscript(file.name, text))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to parse the file')
    }
  }

  function update(index: number, patch: Partial<CourseCandidate>) {
    setCandidates((rows) => rows.map((row, i) => (i === index ? { ...row, ...patch, issues: [] } : row)))
  }

  async function save() {
    setSaving(true)
    setError('')
    try {
      await api('/api/v1/wallet/assets', {
        method: 'POST',
        body: JSON.stringify({
          assets: candidates.map((row) => ({
            sourceType: row.sourceType,
            sourceIdentifier: row.sourceIdentifier || row.courseCode,
            institutionName: row.institutionName,
            courseCode: row.courseCode,
            courseTitle: row.courseTitle,
            credits: row.credits,
            grade: row.grade,
            term: row.term,
            completionDate: row.completionDate || null,
            transcriptOrigin: row.transcriptOrigin,
            originalLearningSource: row.originalLearningSource || row.institutionName,
            verifiedByStudent: true,
          })),
        }),
      })
      navigate('/plan')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Wallet save failed')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Local transcript import</p>
        <h2 className="serif text-3xl">Parse on this device. Review before anything is saved.</h2>
      </div>
      <Alert tone="info">The original file never leaves the browser. Only the rows you verify are sent to Degree Dean.</Alert>
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Card>
        <input
          type="file"
          accept=".csv,.tsv,.txt,.pdf"
          onChange={(e) => {
            const file = e.target.files?.[0]
            if (file) onFile(file)
          }}
        />
        <p className="mt-3 text-sm text-slate">
          Need a starting file?{' '}
          <a className="font-semibold text-gold-dark" href="/sample-transcript.csv" download>
            Download the sample transcript
          </a>
          .
        </p>
      </Card>
      {candidates.length > 0 && (
        <Card className="overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead>
              <tr className="text-xs uppercase tracking-wide text-slate">
                <th className="pb-2 pr-3">Institution</th>
                <th className="pb-2 pr-3">Code</th>
                <th className="pb-2 pr-3">Title</th>
                <th className="pb-2 pr-3">Cr</th>
                <th className="pb-2 pr-3">Type</th>
                <th className="pb-2">Issues</th>
              </tr>
            </thead>
            <tbody>
              {candidates.map((row, index) => (
                <tr key={`${row.courseCode}-${index}`} className="border-t border-obsidian/5 align-top">
                  <td className="py-2 pr-3"><Input value={row.institutionName} onChange={(e) => update(index, { institutionName: e.target.value, transcriptOrigin: e.target.value })} /></td>
                  <td className="py-2 pr-3"><Input value={row.courseCode} onChange={(e) => update(index, { courseCode: e.target.value, sourceIdentifier: e.target.value })} /></td>
                  <td className="py-2 pr-3"><Input value={row.courseTitle} onChange={(e) => update(index, { courseTitle: e.target.value })} /></td>
                  <td className="py-2 pr-3 w-20"><Input type="number" value={row.credits} onChange={(e) => update(index, { credits: Number(e.target.value) })} /></td>
                  <td className="py-2 pr-3">
                    <select
                      className="w-full rounded-md border border-obsidian/15 px-2 py-2"
                      value={row.sourceType}
                      onChange={(e) => update(index, { sourceType: e.target.value as CourseCandidate['sourceType'] })}
                    >
                      <option>INSTITUTION_COURSE</option>
                      <option>PROVIDER_COURSE</option>
                      <option>EXAM</option>
                      <option>CERTIFICATION</option>
                      <option>MILITARY</option>
                      <option>EMPLOYER_TRAINING</option>
                      <option>PLA</option>
                    </select>
                  </td>
                  <td className="py-2 text-burgundy">{row.issues.join(' ')}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="mt-4 flex justify-end">
            <Button disabled={saving || candidates.some((c) => c.issues.length > 0 || !c.courseTitle)} onClick={save}>
              Verify and save to Academic Wallet
            </Button>
          </div>
        </Card>
      )}
      <EstimateNote>Client-parsed rows are re-validated on the server. Unverified rows are rejected.</EstimateNote>
    </div>
  )
}

export function WalletPage() {
  const [assets, setAssets] = useState<Array<Record<string, unknown>>>([])
  const [error, setError] = useState('')

  useEffect(() => {
    api<Array<Record<string, unknown>>>('/api/v1/wallet').then(setAssets).catch((err) => setError(err.message))
  }, [])

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Academic Wallet</p>
        <h2 className="serif text-3xl">Structured assets, not the original transcript.</h2>
      </div>
      {error ? <Alert tone="error">{error}</Alert> : null}
      {assets.length === 0 ? (
        <Card>
          <p className="text-sm text-slate">The wallet is empty. Import a CSV/TSV/TXT file and verify each course first.</p>
        </Card>
      ) : (
        <Card className="overflow-x-auto">
          <table className="min-w-full text-left text-sm">
            <thead>
              <tr className="text-xs uppercase tracking-wide text-slate">
                <th className="pb-2">Source</th>
                <th className="pb-2">Course</th>
                <th className="pb-2">Credits</th>
                <th className="pb-2">Origin</th>
                <th className="pb-2">Verified</th>
              </tr>
            </thead>
            <tbody>
              {assets.map((asset) => (
                <tr key={String(asset.id)} className="border-t border-obsidian/5">
                  <td className="py-2 pr-3">{String(asset.sourceType)}</td>
                  <td className="py-2 pr-3">
                    <span className="font-semibold">{String(asset.courseCode || '')}</span> {String(asset.courseTitle)}
                  </td>
                  <td className="py-2 pr-3">{String(asset.credits)}</td>
                  <td className="py-2 pr-3">{String(asset.originalLearningSource || asset.institutionName || '')}</td>
                  <td className="py-2">{asset.verifiedByStudent ? 'Reviewed' : 'Pending'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  )
}
