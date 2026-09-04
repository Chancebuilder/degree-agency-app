export type Program = {
  id: string;
  title: string;
  university: string;
  country: string;
  degree: "Bachelor" | "Master" | "PhD";
  discipline: string;
  durationMonths: number;
  tuitionUsdPerYear: number;
  summary: string;
};

export const programs: Program[] = [
  {
    id: "cs-mit-ms",
    title: "MS in Computer Science",
    university: "Institute of Technology",
    country: "United States",
    degree: "Master",
    discipline: "Computer Science",
    durationMonths: 24,
    tuitionUsdPerYear: 58000,
    summary:
      "Advanced coursework in systems, AI, and theory with a thesis or capstone track.",
  },
  {
    id: "ba-ox-ppe",
    title: "BA in Philosophy, Politics & Economics",
    university: "Oxbridge College",
    country: "United Kingdom",
    degree: "Bachelor",
    discipline: "Social Sciences",
    durationMonths: 36,
    tuitionUsdPerYear: 41000,
    summary:
      "A flagship interdisciplinary degree combining rigorous analysis across three fields.",
  },
  {
    id: "msc-eth-ds",
    title: "MSc in Data Science",
    university: "Federal Polytechnic",
    country: "Switzerland",
    degree: "Master",
    discipline: "Data Science",
    durationMonths: 18,
    tuitionUsdPerYear: 1600,
    summary:
      "Statistics, machine learning, and large-scale data engineering with industry projects.",
  },
  {
    id: "phd-nus-bio",
    title: "PhD in Bioengineering",
    university: "National University",
    country: "Singapore",
    degree: "PhD",
    discipline: "Engineering",
    durationMonths: 48,
    tuitionUsdPerYear: 9000,
    summary:
      "Fully-funded research program at the intersection of biology and engineering.",
  },
  {
    id: "mba-insead",
    title: "Global MBA",
    university: "Business School Europe",
    country: "France",
    degree: "Master",
    discipline: "Business",
    durationMonths: 12,
    tuitionUsdPerYear: 98000,
    summary:
      "An accelerated one-year MBA with campuses across Europe and Asia.",
  },
  {
    id: "beng-uoft-mech",
    title: "BEng in Mechanical Engineering",
    university: "Maple University",
    country: "Canada",
    degree: "Bachelor",
    discipline: "Engineering",
    durationMonths: 48,
    tuitionUsdPerYear: 42000,
    summary:
      "Hands-on engineering education with co-op placements at leading manufacturers.",
  },
];

export function getProgram(id: string): Program | undefined {
  return programs.find((p) => p.id === id);
}
