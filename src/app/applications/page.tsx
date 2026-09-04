import type { Metadata } from "next";
import Link from "next/link";
import { listApplications } from "@/lib/store";

export const metadata: Metadata = {
  title: "Applications — Degree Agency",
};

export const dynamic = "force-dynamic";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString("en-US", {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

export default async function ApplicationsPage() {
  const applications = await listApplications();

  return (
    <div className="mx-auto max-w-4xl px-6 py-14">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold text-slate-900">
          Submitted applications
        </h1>
        <Link
          href="/apply"
          className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500"
        >
          New application
        </Link>
      </div>

      {applications.length === 0 ? (
        <div className="mt-10 rounded-xl border border-dashed border-slate-300 bg-white p-12 text-center">
          <p className="text-slate-600">No applications yet.</p>
          <Link
            href="/apply"
            className="mt-3 inline-block text-sm font-semibold text-indigo-600 hover:text-indigo-500"
          >
            Submit the first one →
          </Link>
        </div>
      ) : (
        <ul className="mt-8 space-y-4">
          {applications.map((app) => (
            <li
              key={app.id}
              className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm"
            >
              <div className="flex flex-wrap items-center justify-between gap-2">
                <h2 className="text-lg font-semibold text-slate-900">
                  {app.fullName}
                </h2>
                <time className="text-xs text-slate-400">
                  {formatDate(app.createdAt)}
                </time>
              </div>
              <p className="text-sm text-slate-500">{app.email}</p>
              <p className="mt-2 text-sm">
                <span className="font-medium text-indigo-600">
                  {app.programTitle}
                </span>
              </p>
              {app.message && (
                <p className="mt-2 rounded-lg bg-slate-50 p-3 text-sm text-slate-700">
                  {app.message}
                </p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
