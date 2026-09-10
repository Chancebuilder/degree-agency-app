import type { FormEvent } from 'react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../lib/api'
import { Alert, Button, Card, EstimateNote, Field, Input } from '../ui/primitives'

const DISCLOSURE = `The Degree Agency is not a college, university, registrar, accreditor, or degree-granting institution. Degree Dean does not guarantee admission, credit acceptance, or transfer outcomes. Final authority always rests with the receiving institution. Every cost, date, and credit total in this product is an estimate built from verified policy data that can change.`

const CONSENTS = [
  { purpose: 'DEGREE_PLANNING', required: true, label: 'Degree-planning processing', text: 'Use my reviewed academic assets to calculate remaining requirements, scenarios, and next actions.' },
  { purpose: 'ACCOUNT_STORAGE', required: true, label: 'Account storage', text: 'Store my profile, wallet, and plans so I can return to this work.' },
  { purpose: 'DISCLOSURE_ACKNOWLEDGMENT', required: true, label: 'Product disclosure', text: DISCLOSURE },
  { purpose: 'ADVISOR_ACCESS', required: false, label: 'Advisor access', text: 'Allow a Degree Dean advisor to review my structured academic data if I request a human review later.' },
  { purpose: 'MARKETING', required: false, label: 'Marketing communications', text: 'Send occasional product updates. Not required to use the planner.' },
  { purpose: 'INSTITUTION_MATCHING', required: false, label: 'Institution matching', text: 'Use anonymized remaining-credit data for future college matching. Off by default.' },
  { purpose: 'INSTITUTION_PROFILE_SHARING', required: false, label: 'Institution profile sharing', text: 'Share an approved academic profile with a college only after I opt in later.' },
  { purpose: 'TRANSCRIPT_SHARING', required: false, label: 'Transcript sharing', text: 'I understand original transcripts stay on my device in this MVP. I do not authorize cloud transcript sharing.' },
]

export function OnboardingPage() {
  const navigate = useNavigate()
  const [step, setStep] = useState(1)
  const [dob, setDob] = useState('1988-04-12')
  const [ageMessage, setAgeMessage] = useState('')
  const [eligible, setEligible] = useState(false)
  const [checks, setChecks] = useState<Record<string, boolean>>({
    DEGREE_PLANNING: false,
    ACCOUNT_STORAGE: false,
    DISCLOSURE_ACKNOWLEDGMENT: false,
  })
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [hours, setHours] = useState(12)
  const [budget, setBudget] = useState(400)
  const [error, setError] = useState('')

  async function confirmAge(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      const result = await api<{ eligible: boolean; message?: string }>('/api/v1/onboarding/age', {
        method: 'POST',
        body: JSON.stringify({ dateOfBirth: dob }),
      })
      setEligible(result.eligible)
      setAgeMessage(result.message || '')
      if (result.eligible) setStep(2)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to confirm age')
    }
  }

  async function saveConsents(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await api('/api/v1/consents', {
        method: 'POST',
        body: JSON.stringify({
          items: CONSENTS.map((item) => ({
            purpose: item.purpose,
            granted: Boolean(checks[item.purpose]),
            acknowledgmentText: item.text,
          })),
        }),
      })
      setStep(3)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Consent could not be stored')
    }
  }

  async function saveProfile(event: FormEvent) {
    event.preventDefault()
    setError('')
    try {
      await api('/api/v1/profile', {
        method: 'PUT',
        body: JSON.stringify({
          firstName,
          lastName,
          weeklyStudyHours: hours,
          monthlyBudget: budget,
          intensity: 'STANDARD',
          maxConcurrent: 2,
          competencyWilling: true,
          examWilling: true,
          startDate: new Date().toISOString().slice(0, 10),
        }),
      })
      navigate('/goal')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Profile could not be saved')
    }
  }

  return (
    <div className="space-y-4">
      <div>
        <p className="text-xs uppercase tracking-[0.16em] text-gold-dark">Onboarding</p>
        <h2 className="serif text-3xl text-obsidian">Let’s establish the ground rules.</h2>
      </div>
      {error ? <Alert tone="error">{error}</Alert> : null}

      {step === 1 && (
        <Card>
          <h3 className="serif text-xl">Age eligibility</h3>
          <p className="mt-2 text-sm text-slate">Degree Dean is designed for adults 18 and older. Transcript collection does not proceed for minors.</p>
          <form className="mt-4 space-y-4" onSubmit={confirmAge}>
            <Field label="Date of birth">
              <Input type="date" value={dob} onChange={(e) => setDob(e.target.value)} required />
            </Field>
            <Button type="submit">Confirm age</Button>
          </form>
          {ageMessage ? <div className="mt-4"><Alert tone="warn">{ageMessage}</Alert></div> : null}
        </Card>
      )}

      {step === 2 && eligible && (
        <Card>
          <h3 className="serif text-xl">Consent and disclosure</h3>
          <p className="mt-2 text-sm text-slate">Purpose-specific consent. Required items cannot be skipped.</p>
          <form className="mt-4 space-y-4" onSubmit={saveConsents}>
            {CONSENTS.map((item) => (
              <label key={item.purpose} className="flex gap-3 rounded-lg border border-obsidian/10 p-3">
                <input
                  type="checkbox"
                  className="mt-1"
                  checked={Boolean(checks[item.purpose])}
                  onChange={(e) => setChecks((prev) => ({ ...prev, [item.purpose]: e.target.checked }))}
                />
                <span>
                  <span className="block text-sm font-semibold">
                    {item.label} {item.required ? <span className="text-burgundy">Required</span> : <span className="text-slate">Optional</span>}
                  </span>
                  <span className="mt-1 block text-sm text-slate">{item.text}</span>
                </span>
              </label>
            ))}
            <EstimateNote />
            <Button type="submit">Store consent</Button>
          </form>
        </Card>
      )}

      {step === 3 && (
        <Card>
          <h3 className="serif text-xl">Your working profile</h3>
          <form className="mt-4 grid gap-4 sm:grid-cols-2" onSubmit={saveProfile}>
            <Field label="First name"><Input value={firstName} onChange={(e) => setFirstName(e.target.value)} required /></Field>
            <Field label="Last name"><Input value={lastName} onChange={(e) => setLastName(e.target.value)} required /></Field>
            <Field label="Weekly study hours"><Input type="number" min={1} max={60} value={hours} onChange={(e) => setHours(Number(e.target.value))} /></Field>
            <Field label="Monthly budget (USD)"><Input type="number" min={0} value={budget} onChange={(e) => setBudget(Number(e.target.value))} /></Field>
            <div className="sm:col-span-2"><Button type="submit">Save profile and choose a degree</Button></div>
          </form>
        </Card>
      )}
    </div>
  )
}
