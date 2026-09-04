import { NextResponse } from "next/server";
import { addApplication, listApplications } from "@/lib/store";
import { getProgram } from "@/lib/programs";

export async function GET() {
  const applications = await listApplications();
  return NextResponse.json({ applications });
}

export async function POST(request: Request) {
  let body: unknown;
  try {
    body = await request.json();
  } catch {
    return NextResponse.json({ error: "Invalid JSON body." }, { status: 400 });
  }

  const { fullName, email, programId, message } = (body ?? {}) as Record<
    string,
    unknown
  >;

  const errors: Record<string, string> = {};
  if (typeof fullName !== "string" || fullName.trim().length < 2) {
    errors.fullName = "Please enter your full name.";
  }
  if (
    typeof email !== "string" ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())
  ) {
    errors.email = "Please enter a valid email address.";
  }
  const program =
    typeof programId === "string" ? getProgram(programId) : undefined;
  if (!program) {
    errors.programId = "Please choose a valid program.";
  }

  if (Object.keys(errors).length > 0) {
    return NextResponse.json({ errors }, { status: 422 });
  }

  const application = await addApplication(
    {
      fullName: (fullName as string).trim(),
      email: (email as string).trim(),
      programId: programId as string,
      message: typeof message === "string" ? message.trim() : "",
    },
    program!.title,
  );

  return NextResponse.json({ application }, { status: 201 });
}
