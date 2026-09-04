import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../lib/api'
import { Alert, Button, Card, EstimateNote } from '../ui/primitives'

type Institution = {
  id: string
  name: string
  shortName: string
  code: string
  tier: string
  supported: boolean
  deliveryModel: string
  lastVerifiedAt: string
}

type Program = {
  id: string
  institutionId: string
  name: string
  degreeFamily: string
  catalogYear: string
  totalCredits: number
}

export function GoalPage() {
  const navigate = useNavigate()
  const [institutions, setInstitutions] = useState<Institution[]>([])
  const [programs, setPrograms] = useState<Program[]>([])
  const [family, setFamily] = useState('BUSINESS_ADMINISTRATION')
  const [institutionId, setInstitutionId] = useState('')
  const [programId, setProgramId] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    api<Institution[]>('/api/v1/institutions').then(setInstitutions).catch((err) => setError(err.message))
    api<Program[]>('/api/v1/programs').then(setPrograms).catch((err) => setError(err.message))
  }, [])

  const familyInstitutions = institutions.filter((institution) =>
    programs.some((program) => program.degreeFamily === family && program.institutionId === institution.id),
  )
  const visiblePrograms = programs.filter((p) => p.degreeFamily === family && p.institutionId === institutionId)

  async function save() {
    setError('')
    try {
      await api('/api/v1/goals', {
        method: 'POST',
        body: JSON.stringify({ degreeFamily: family, institutionId, programId }),
      })
      navigate('/import')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not save the goal')
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Degree goal</p>
        <h2 className="serif text-3xl">Choose a bachelor’s lane and one Tier A school.</h2>
      </div>
      {error ? <Alert tone="error">{error}</Alert> : null}
      <Card>
        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <p className="text-sm font-semibold">Degree family</p>
            <div className="mt-2 space-y-2">
              {[
                ['BUSINESS_ADMINISTRATION', 'Business Administration'],
                ['CYBERSECURITY', 'Cybersecurity / IT'],
                ['PROFESSIONAL_STUDIES', 'Professional / Liberal / General Studies'],
              ].map(([value, label]) => (
                <label key={value} className="flex items-center gap-2 text-sm">
                  <input type="radio" name="family" checked={family === value} onChange={() => { setFamily(value); setInstitutionId(''); setProgramId('') }} />
                  {label}
                </label>
              ))}
            </div>
          </div>
          <div>
            <p className="text-sm font-semibold">Candidate institution</p>
            <div className="mt-2 space-y-2">
              {familyInstitutions.map((institution) => (
                <button
                  key={institution.id}
                  type="button"
                  onClick={() => { setInstitutionId(institution.id); setProgramId('') }}
                  className={`w-full rounded-lg border px-3 py-3 text-left ${institutionId === institution.id ? 'border-gold bg-[#FBF4E4]' : 'border-obsidian/10'}`}
                >
                  <span className="block text-sm font-semibold">{institution.shortName || institution.name}</span>
                  <span className="block text-xs text-slate">Tier {institution.tier} · {institution.deliveryModel.replaceAll('_', ' ')} · verified {institution.lastVerifiedAt}</span>
                </button>
              ))}
            </div>
          </div>
        </div>
        <div className="mt-5">
          <p className="text-sm font-semibold">Program and catalog year</p>
          <div className="mt-2 space-y-2">
            {visiblePrograms.map((program) => (
              <label key={program.id} className="flex items-start gap-2 rounded-lg border border-obsidian/10 p-3 text-sm">
                <input type="radio" name="program" checked={programId === program.id} onChange={() => setProgramId(program.id)} />
                <span>
                  <span className="block font-semibold">{program.name}</span>
                  <span className="text-slate">{program.totalCredits} credits · catalog {program.catalogYear}</span>
                </span>
              </label>
            ))}
            {institutionId && visiblePrograms.length === 0 ? <p className="text-sm text-slate">No programs in this family at the selected school.</p> : null}
          </div>
        </div>
        <div className="mt-5 flex items-center justify-between gap-4">
          <EstimateNote>Tier B and C schools never enter a plan. Only Tier A is selectable here.</EstimateNote>
          <Button disabled={!institutionId || !programId} onClick={save}>Save goal</Button>
        </div>
      </Card>
    </div>
  )
}
