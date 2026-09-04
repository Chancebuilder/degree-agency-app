import { describe, expect, it } from 'vitest'
import { PdfTranscriptParser, parseTranscript, StructuredTranscriptParser } from './transcriptParser'

const sample = `institution,course_code,course_title,credits,grade,term,completion_date,source_type
Sophia Learning,SOPH-STAT1001,Introduction to Statistics,3,A,2023-02,2023-03-20,PROVIDER_COURSE
Central Community College,ENGL101,English Composition I,3,B,2018-FA,2018-12-15,INSTITUTION_COURSE
`

describe('StructuredTranscriptParser', () => {
  const parser = new StructuredTranscriptParser()

  it('accepts csv tsv and txt', () => {
    expect(parser.canParse('wallet.csv', sample)).toBe(true)
    expect(parser.canParse('wallet.tsv', 'course_title\tcredits')).toBe(true)
    expect(parser.canParse('notes.txt', 'title,credits')).toBe(true)
    expect(parser.canParse('scan.pdf', sample)).toBe(false)
  })

  it('parses reviewed course candidates and flags bad credits', () => {
    const rows = parser.parse('sample.csv', sample)
    expect(rows).toHaveLength(2)
    expect(rows[0].sourceType).toBe('PROVIDER_COURSE')
    expect(rows[1].courseCode).toBe('ENGL101')
    const broken = parser.parse('bad.csv', 'course_title,credits\nStats,abc')
    expect(broken[0].issues[0]).toMatch(/Credits/)
  })
})

describe('PdfTranscriptParser', () => {
  it('refuses to fake OCR', () => {
    const parser = new PdfTranscriptParser()
    expect(parser.canParse('scan.pdf')).toBe(true)
    expect(() => parser.parse()).toThrow(/Phase 2/)
  })
})

describe('parseTranscript', () => {
  it('routes by filename', () => {
    expect(parseTranscript('sample.csv', sample)[0].courseTitle).toBe('Introduction to Statistics')
    expect(() => parseTranscript('scan.pdf', 'binary')).toThrow(/Phase 2/)
  })
})
