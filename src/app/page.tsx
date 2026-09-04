import Link from "next/link";
import { programs } from "@/lib/programs";
import { ProgramCard } from "@/components/program-card";

export default function Home() {
  const featured = programs.slice(0, 3);

  return (
    <div>
      <section className="bg-gradient-to-b from-indigo-600 to-indigo-700 text-white">
        <div className="mx-auto max-w-6xl px-6 py-20 sm:py-28">
          <p className="text-sm font-semibold uppercase tracking-wider text-indigo-200">
            Study abroad, made simple
          </p>
          <h1 className="mt-3 max-w-3xl text-4xl font-bold leading-tight sm:text-5xl">
            Find your degree. Apply with confidence.
          </h1>
          <p className="mt-5 max-w-2xl text-lg text-indigo-100">
            Degree Agency connects students with top universities worldwide.
            Explore programs, get expert guidance, and submit your application
            in minutes.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link
              href="/programs"
              className="rounded-lg bg-white px-5 py-3 text-sm font-semibold text-indigo-700 transition hover:bg-indigo-50"
            >
              Browse programs
            </Link>
            <Link
              href="/apply"
              className="rounded-lg border border-indigo-300 px-5 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500"
            >
              Start your application
            </Link>
          </div>
          <dl className="mt-12 grid max-w-lg grid-cols-3 gap-6 text-center">
            <div>
              <dt className="text-3xl font-bold">120+</dt>
              <dd className="text-sm text-indigo-200">Partner universities</dd>
            </div>
            <div>
              <dt className="text-3xl font-bold">30</dt>
              <dd className="text-sm text-indigo-200">Countries</dd>
            </div>
            <div>
              <dt className="text-3xl font-bold">98%</dt>
              <dd className="text-sm text-indigo-200">Visa success rate</dd>
            </div>
          </dl>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-6 py-16">
        <div className="flex items-end justify-between">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">
              Featured programs
            </h2>
            <p className="mt-1 text-slate-600">
              A selection of popular degrees from our partner universities.
            </p>
          </div>
          <Link
            href="/programs"
            className="hidden text-sm font-semibold text-indigo-600 hover:text-indigo-500 sm:block"
          >
            View all →
          </Link>
        </div>
        <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {featured.map((program) => (
            <ProgramCard key={program.id} program={program} />
          ))}
        </div>
      </section>
    </div>
  );
}
