import Link from "next/link";

export function SiteHeader() {
  return (
    <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <Link href="/" className="flex items-center gap-2">
          <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600 text-sm font-bold text-white">
            DA
          </span>
          <span className="text-lg font-semibold tracking-tight">
            Degree Agency
          </span>
        </Link>
        <nav className="flex items-center gap-1 text-sm font-medium text-slate-600 sm:gap-4">
          <Link
            href="/programs"
            className="rounded-md px-3 py-2 transition hover:bg-slate-100 hover:text-slate-900"
          >
            Programs
          </Link>
          <Link
            href="/applications"
            className="rounded-md px-3 py-2 transition hover:bg-slate-100 hover:text-slate-900"
          >
            Applications
          </Link>
          <Link
            href="/apply"
            className="rounded-md bg-indigo-600 px-4 py-2 text-white transition hover:bg-indigo-500"
          >
            Apply now
          </Link>
        </nav>
      </div>
    </header>
  );
}
