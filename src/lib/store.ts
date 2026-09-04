import { promises as fs } from "fs";
import path from "path";
import { randomUUID } from "crypto";

export type Application = {
  id: string;
  fullName: string;
  email: string;
  programId: string;
  programTitle: string;
  message: string;
  createdAt: string;
};

export type NewApplication = Omit<
  Application,
  "id" | "createdAt" | "programTitle"
>;

const dataDir = path.join(process.cwd(), "data");
const dataFile = path.join(dataDir, "applications.json");

async function readAll(): Promise<Application[]> {
  try {
    const raw = await fs.readFile(dataFile, "utf8");
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? (parsed as Application[]) : [];
  } catch {
    return [];
  }
}

async function writeAll(items: Application[]): Promise<void> {
  await fs.mkdir(dataDir, { recursive: true });
  await fs.writeFile(dataFile, JSON.stringify(items, null, 2), "utf8");
}

export async function listApplications(): Promise<Application[]> {
  const items = await readAll();
  return items.sort((a, b) => b.createdAt.localeCompare(a.createdAt));
}

export async function addApplication(
  input: NewApplication,
  programTitle: string,
): Promise<Application> {
  const items = await readAll();
  const application: Application = {
    id: randomUUID(),
    fullName: input.fullName,
    email: input.email,
    programId: input.programId,
    programTitle,
    message: input.message,
    createdAt: new Date().toISOString(),
  };
  items.push(application);
  await writeAll(items);
  return application;
}
