import { NextResponse } from "next/server";
import { programs } from "@/lib/programs";

export function GET() {
  return NextResponse.json({ programs });
}
