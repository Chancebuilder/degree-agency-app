import type { Metadata } from "next";
import { programs } from "@/lib/programs";
import { ProgramCard } from "@/components/program-card";

export const metadata: Metadata = {
  title: "Programs — Degree Agency",
};

export default function ProgramsPage() {
  return (
    <div className="mx-auto max-w-6xl px-6 py-14">
      <h1 className="text-3xl font-bold text-slate-900">Explore programs</h1>
      <p className="mt-2 max-w-2xl text-slate-600">
        Browse {programs.length} degree programs across our global network of
        partner universities.
      </p>
      <div className="mt-10 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {programs.map((program) => (
          <ProgramCard key={program.id} program={program} />
        ))}
      </div>
    </div>
  );
}
