import type { Metadata } from "next";
import { programs } from "@/lib/programs";
import { ApplyForm } from "@/components/apply-form";

export const metadata: Metadata = {
  title: "Apply — Degree Agency",
};

export default async function ApplyPage({
  searchParams,
}: PageProps<"/apply">) {
  const params = await searchParams;
  const raw = params?.program;
  const initialProgramId = Array.isArray(raw) ? raw[0] : raw;

  const options = programs.map((p) => ({
    id: p.id,
    title: p.title,
    university: p.university,
  }));

  return (
    <div className="mx-auto max-w-2xl px-6 py-14">
      <h1 className="text-3xl font-bold text-slate-900">
        Start your application
      </h1>
      <p className="mt-2 text-slate-600">
        Fill in your details and our advisors will guide you through the next
        steps.
      </p>
      <div className="mt-8">
        <ApplyForm programs={options} initialProgramId={initialProgramId} />
      </div>
    </div>
  );
}
