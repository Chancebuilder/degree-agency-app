"use client";

import { useState } from "react";
import Link from "next/link";
import type { Program } from "@/lib/programs";

type Props = {
  programs: Pick<Program, "id" | "title" | "university">[];
  initialProgramId?: string;
};

type FieldErrors = Partial<Record<"fullName" | "email" | "programId", string>>;

export function ApplyForm({ programs, initialProgramId }: Props) {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [programId, setProgramId] = useState(
    initialProgramId && programs.some((p) => p.id === initialProgramId)
      ? initialProgramId
      : "",
  );
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState<FieldErrors>({});
  const [status, setStatus] = useState<"idle" | "submitting" | "success">(
    "idle",
  );
  const [submittedName, setSubmittedName] = useState("");

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setStatus("submitting");
    setErrors({});

    const res = await fetch("/api/applications", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ fullName, email, programId, message }),
    });

    if (res.status === 201) {
      setSubmittedName(fullName);
      setStatus("success");
      setFullName("");
      setEmail("");
      setProgramId("");
      setMessage("");
      return;
    }

    const data = await res.json().catch(() => ({}));
    setErrors(data.errors ?? {});
    setStatus("idle");
  }

  if (status === "success") {
    return (
      <div
        role="status"
        className="rounded-xl border border-emerald-200 bg-emerald-50 p-8 text-center"
      >
        <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-emerald-600 text-2xl text-white">
          ✓
        </div>
        <h2 className="mt-4 text-xl font-semibold text-emerald-900">
          Application submitted!
        </h2>
        <p className="mt-2 text-emerald-800">
          Thanks, {submittedName}. Our advisors will be in touch shortly.
        </p>
        <div className="mt-6 flex justify-center gap-3">
          <Link
            href="/applications"
            className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-emerald-500"
          >
            View submitted applications
          </Link>
          <button
            onClick={() => setStatus("idle")}
            className="rounded-lg border border-emerald-300 px-4 py-2 text-sm font-medium text-emerald-800 transition hover:bg-emerald-100"
          >
            Submit another
          </button>
        </div>
      </div>
    );
  }

  return (
    <form
      onSubmit={handleSubmit}
      className="space-y-5 rounded-xl border border-slate-200 bg-white p-6 shadow-sm sm:p-8"
    >
      <div>
        <label
          htmlFor="fullName"
          className="block text-sm font-medium text-slate-700"
        >
          Full name
        </label>
        <input
          id="fullName"
          name="fullName"
          type="text"
          value={fullName}
          onChange={(e) => setFullName(e.target.value)}
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
          placeholder="Ada Lovelace"
        />
        {errors.fullName && (
          <p className="mt-1 text-sm text-red-600">{errors.fullName}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="email"
          className="block text-sm font-medium text-slate-700"
        >
          Email
        </label>
        <input
          id="email"
          name="email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
          placeholder="ada@example.com"
        />
        {errors.email && (
          <p className="mt-1 text-sm text-red-600">{errors.email}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="programId"
          className="block text-sm font-medium text-slate-700"
        >
          Program
        </label>
        <select
          id="programId"
          name="programId"
          value={programId}
          onChange={(e) => setProgramId(e.target.value)}
          className="mt-1 w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
        >
          <option value="">Select a program…</option>
          {programs.map((p) => (
            <option key={p.id} value={p.id}>
              {p.title} — {p.university}
            </option>
          ))}
        </select>
        {errors.programId && (
          <p className="mt-1 text-sm text-red-600">{errors.programId}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="message"
          className="block text-sm font-medium text-slate-700"
        >
          Message <span className="text-slate-400">(optional)</span>
        </label>
        <textarea
          id="message"
          name="message"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          rows={4}
          className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200"
          placeholder="Tell us about your goals…"
        />
      </div>

      <button
        type="submit"
        disabled={status === "submitting"}
        className="w-full rounded-lg bg-indigo-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {status === "submitting" ? "Submitting…" : "Submit application"}
      </button>
    </form>
  );
}
