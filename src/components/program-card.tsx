import Link from "next/link";
import type { Program } from "@/lib/programs";

const degreeStyles: Record<Program["degree"], string> = {
  Bachelor: "bg-emerald-100 text-emerald-700",
  Master: "bg-indigo-100 text-indigo-700",
  PhD: "bg-amber-100 text-amber-700",
};

function formatTuition(usdPerYear: number): string {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(usdPerYear);
}

export function ProgramCard({ program }: { program: Program }) {
  return (
    <article className="flex flex-col rounded-xl border border-slate-200 bg-white p-6 shadow-sm transition hover:shadow-md">
      <div className="flex items-center justify-between gap-2">
        <span
          className={`rounded-full px-2.5 py-1 text-xs font-semibold ${degreeStyles[program.degree]}`}
        >
          {program.degree}
        </span>
        <span className="text-xs font-medium text-slate-500">
          {program.country}
        </span>
      </div>
      <h3 className="mt-3 text-lg font-semibold leading-snug text-slate-900">
        {program.title}
      </h3>
      <p className="text-sm font-medium text-indigo-600">
        {program.university}
      </p>
      <p className="mt-2 flex-1 text-sm text-slate-600">{program.summary}</p>
      <dl className="mt-4 grid grid-cols-2 gap-2 text-sm">
        <div>
          <dt className="text-slate-400">Duration</dt>
          <dd className="font-medium text-slate-700">
            {program.durationMonths} months
          </dd>
        </div>
        <div>
          <dt className="text-slate-400">Tuition / yr</dt>
          <dd className="font-medium text-slate-700">
            {formatTuition(program.tuitionUsdPerYear)}
          </dd>
        </div>
      </dl>
      <Link
        href={{ pathname: "/apply", query: { program: program.id } }}
        className="mt-5 inline-flex items-center justify-center rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-700"
      >
        Apply to this program
      </Link>
    </article>
  );
}
