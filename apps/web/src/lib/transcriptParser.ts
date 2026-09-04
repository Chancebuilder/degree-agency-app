export type SourceType =
  | 'INSTITUTION_COURSE'
  | 'PROVIDER_COURSE'
  | 'EXAM'
  | 'CERTIFICATION'
  | 'MILITARY'
  | 'EMPLOYER_TRAINING'
  | 'PLA'

export type CourseCandidate = {
  institutionName: string
  courseCode: string
  courseTitle: string
  credits: number
  grade: string
  term: string
  completionDate: string
  sourceType: SourceType
  sourceIdentifier: string
  transcriptOrigin: string
  originalLearningSource: string
  issues: string[]
}

export interface TranscriptParser {
  canParse(filename: string, text: string): boolean
  parse(filename: string, text: string): CourseCandidate[]
}

const SOURCE_TYPES: SourceType[] = [
  'INSTITUTION_COURSE',
  'PROVIDER_COURSE',
  'EXAM',
  'CERTIFICATION',
  'MILITARY',
  'EMPLOYER_TRAINING',
  'PLA',
]

function detectDelimiter(header: string): string {
  if (header.includes('\t')) return '\t'
  return ','
}

function splitLine(line: string, delimiter: string): string[] {
  const out: string[] = []
  let current = ''
  let quoted = false
  for (let i = 0; i < line.length; i += 1) {
    const ch = line[i]
    if (ch === '"') {
      quoted = !quoted
    } else if (ch === delimiter && !quoted) {
      out.push(current.trim())
      current = ''
    } else {
      current += ch
    }
  }
  out.push(current.trim())
  return out
}

function normalizeHeader(value: string) {
  return value.toLowerCase().replace(/[^a-z0-9]/g, '')
}

function headerIndex(headers: string[], aliases: string[]) {
  const normalized = headers.map(normalizeHeader)
  for (const alias of aliases) {
    const idx = normalized.indexOf(normalizeHeader(alias))
    if (idx >= 0) return idx
  }
  return -1
}

function inferSourceType(institution: string, code: string, explicit?: string): SourceType {
  const raw = (explicit || '').toUpperCase().replace(/\s+/g, '_')
  if (SOURCE_TYPES.includes(raw as SourceType)) return raw as SourceType
  if (/clep|dsst/i.test(institution) || /^CLEP-/.test(code)) return 'EXAM'
  if (/sophia|study\.com|straighterline/i.test(institution) || /^(SOPH|SDC|SL)-/.test(code)) return 'PROVIDER_COURSE'
  return 'INSTITUTION_COURSE'
}

export class StructuredTranscriptParser implements TranscriptParser {
  canParse(filename: string, text: string): boolean {
    const lower = filename.toLowerCase()
    if (!(lower.endsWith('.csv') || lower.endsWith('.tsv') || lower.endsWith('.txt'))) return false
    const first = text.split(/\r?\n/).find((line) => line.trim().length > 0) || ''
    return /course|title|credit/i.test(first)
  }

  parse(filename: string, text: string): CourseCandidate[] {
    const lines = text.split(/\r?\n/).filter((line) => line.trim().length > 0)
    if (lines.length < 2) {
      throw new Error('The file needs a header row and at least one course row.')
    }
    const delimiter = filename.toLowerCase().endsWith('.tsv') ? '\t' : detectDelimiter(lines[0])
    const headers = splitLine(lines[0], delimiter)
    const inst = headerIndex(headers, ['institution', 'school', 'college'])
    const code = headerIndex(headers, ['course_code', 'coursecode', 'code', 'subject'])
    const title = headerIndex(headers, ['course_title', 'title', 'coursename', 'name'])
    const credits = headerIndex(headers, ['credits', 'credit', 'hours', 'units'])
    const grade = headerIndex(headers, ['grade'])
    const term = headerIndex(headers, ['term', 'semester'])
    const date = headerIndex(headers, ['completion_date', 'date', 'completed'])
    const source = headerIndex(headers, ['source_type', 'sourcetype', 'type'])
    if (title < 0 || credits < 0) {
      throw new Error('Required columns are missing. Include at least course title and credits.')
    }

    return lines.slice(1).map((line, index) => {
      const cols = splitLine(line, delimiter)
      const institutionName = inst >= 0 ? cols[inst] || '' : ''
      const courseCode = code >= 0 ? cols[code] || '' : ''
      const courseTitle = cols[title] || ''
      const creditRaw = cols[credits] || ''
      const creditNum = Number.parseFloat(creditRaw)
      const issues: string[] = []
      if (!courseTitle) issues.push('Missing title')
      if (!Number.isFinite(creditNum) || creditNum <= 0) issues.push('Credits must be a positive number')
      if (creditNum > 20) issues.push('Credits look unusually high')
      const sourceType = inferSourceType(institutionName, courseCode, source >= 0 ? cols[source] : undefined)
      return {
        institutionName,
        courseCode,
        courseTitle,
        credits: Number.isFinite(creditNum) ? creditNum : 0,
        grade: grade >= 0 ? cols[grade] || '' : '',
        term: term >= 0 ? cols[term] || '' : '',
        completionDate: date >= 0 ? cols[date] || '' : '',
        sourceType,
        sourceIdentifier: courseCode,
        transcriptOrigin: institutionName,
        originalLearningSource: institutionName,
        issues: issues.length ? [`Row ${index + 2}: ${issues.join('; ')}`] : [],
      }
    })
  }
}

export class PdfTranscriptParser implements TranscriptParser {
  canParse(filename: string): boolean {
    return filename.toLowerCase().endsWith('.pdf')
  }

  parse(): CourseCandidate[] {
    throw new Error('PDF and OCR transcript parsing is Phase 2. Use a CSV, TSV, or TXT export for the MVP.')
  }
}

export function parseTranscript(filename: string, text: string): CourseCandidate[] {
  const parsers: TranscriptParser[] = [new StructuredTranscriptParser(), new PdfTranscriptParser()]
  const parser = parsers.find((candidate) => candidate.canParse(filename, text))
  if (!parser) {
    throw new Error('Unsupported file. Upload a CSV, TSV, or TXT transcript export.')
  }
  return parser.parse(filename, text)
}
