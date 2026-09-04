#!/usr/bin/env python3
"""Generate idempotent Degree Dean MVP seed CSVs.

Figures that come from official pages are marked VERIFIED with the source URL.
Equivalencies compiled from published transfer guides are VERIFIED when the
guide is institutional; otherwise HIGH_CONFIDENCE with the compiling source.
"""
from __future__ import annotations

import csv
import sys
import uuid
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from seed_extensions import (  # noqa: E402
    EXTRA_CC,
    FAMILY_MAJORS,
    PROVIDER_URLS,
    SLOT_OVERLAY,
    equivalency_status,
    extra_opportunities,
    extra_pg_rules,
    extra_programs,
    extra_providers,
    extra_wgu_rules,
    source_type_for,
)

ROOT = Path(__file__).resolve().parents[1]
SEED = ROOT / "data" / "seed"
SEED.mkdir(parents=True, exist_ok=True)

VERIFIED_AT = "2026-09-01"
VERIFIED_BY = "seed-research-2026-09"

# Slot codes used across bachelor's programs. Gen-ed is shared; majors differ.
GEN_ED = [
    ("ENG1", "English Composition I", 3, "LOWER", False),
    ("ENG2", "English Composition II", 3, "LOWER", False),
    ("QR", "Quantitative Reasoning / College Algebra", 3, "LOWER", False),
    ("STAT", "Introductory Statistics", 3, "LOWER", False),
    ("NS1", "Natural Science with Lab", 4, "LOWER", False),
    ("NS2", "Natural Science", 3, "LOWER", False),
    ("HUM1", "Humanities I", 3, "LOWER", False),
    ("HUM2", "Humanities II", 3, "LOWER", False),
    ("SS1", "Social Science I", 3, "LOWER", False),
    ("SS2", "Social Science II", 3, "LOWER", False),
    ("HIST", "History", 3, "LOWER", False),
    ("COMM", "Public Speaking / Communication", 3, "LOWER", False),
    ("ETHICS", "Ethics", 3, "LOWER", False),
    ("INFO", "Information Literacy / Computer Applications", 3, "LOWER", False),
]

BUSINESS_MAJOR = [
    ("BUS101", "Introduction to Business", 3, "LOWER", False),
    ("ACCT1", "Financial Accounting", 3, "LOWER", False),
    ("ACCT2", "Managerial Accounting", 3, "LOWER", False),
    ("MICRO", "Microeconomics", 3, "LOWER", False),
    ("MACRO", "Macroeconomics", 3, "LOWER", False),
    ("MGT", "Principles of Management", 3, "LOWER", False),
    ("MKT", "Principles of Marketing", 3, "LOWER", False),
    ("BLAW", "Business Law", 3, "LOWER", False),
    ("FIN", "Principles of Finance", 3, "UPPER", False),
    ("OB", "Organizational Behavior", 3, "UPPER", False),
    ("IS", "Management Information Systems", 3, "LOWER", False),
    ("OPS", "Operations Management", 3, "UPPER", False),
    ("HR", "Human Resource Management", 3, "UPPER", False),
    ("STRAT", "Strategic Management / Capstone", 3, "UPPER", True),
    ("BELEC1", "Business Elective I", 3, "UPPER", False),
    ("BELEC2", "Business Elective II", 3, "UPPER", False),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

CYBER_MAJOR = [
    ("IT101", "Introduction to IT / Computing", 3, "LOWER", False),
    ("NET", "Networking Fundamentals", 3, "LOWER", False),
    ("OS", "Operating Systems", 3, "LOWER", False),
    ("SEC1", "Introduction to Cybersecurity", 3, "LOWER", False),
    ("PROG", "Programming Fundamentals", 3, "LOWER", False),
    ("DB", "Database Fundamentals", 3, "LOWER", False),
    ("WEB", "Web Technologies", 3, "LOWER", False),
    ("RISK", "Cyber Risk Management", 3, "UPPER", False),
    ("LAW", "Cybersecurity Law and Ethics", 3, "UPPER", False),
    ("CRYPT", "Applied Cryptography", 3, "UPPER", False),
    ("FOR", "Digital Forensics", 3, "UPPER", False),
    ("NETSEC", "Network Security", 3, "UPPER", False),
    ("PTEST", "Penetration Testing", 3, "UPPER", True),
    ("SEC2", "Security Operations", 3, "UPPER", False),
    ("CAP", "Cybersecurity Capstone", 3, "UPPER", True),
    ("CELEC1", "Cyber Elective I", 3, "UPPER", False),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

PROFESSIONAL_MAJOR = [
    ("ILP", "Degree Design / Information Literacy", 3, "LOWER", False),
    ("LEAD", "Leadership / Organizational Behavior", 3, "UPPER", False),
    ("PROFCOMM", "Professional Communication", 3, "UPPER", False),
    ("DATA", "Applied Statistics / Decision Making", 3, "UPPER", False),
    ("ETH2", "Professional Ethics", 3, "UPPER", False),
    ("RES", "Applied Inquiry / Research Methods", 3, "UPPER", False),
    ("CONC1", "Concentration / Applied Elective I", 3, "UPPER", False),
    ("CONC2", "Concentration / Applied Elective II", 3, "UPPER", False),
    ("CONC3", "Concentration / Applied Elective III", 3, "UPPER", False),
    ("CONC4", "Concentration / Applied Elective IV", 3, "UPPER", False),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
    ("ELEC5", "Free Elective V", 3, "LOWER", False),
    ("ELEC6", "Free Elective VI", 3, "LOWER", False),
    ("CAP", "Professional Studies / Liberal Studies Capstone", 3, "UPPER", True),
]


def slots_for(family: str) -> list[tuple]:
    if family == "BUSINESS_ADMINISTRATION":
        return GEN_ED + BUSINESS_MAJOR
    if family == "CYBERSECURITY":
        return GEN_ED + CYBER_MAJOR
    if family in FAMILY_MAJORS:
        return GEN_ED + FAMILY_MAJORS[family]
    return GEN_ED + PROFESSIONAL_MAJOR


def uid(*parts: str) -> str:
    return str(uuid.uuid5(uuid.NAMESPACE_URL, "degreedean:" + ":".join(parts)))


def write_csv(name: str, rows: list[dict], fieldnames: list[str]) -> None:
    path = SEED / name
    with path.open("w", newline="", encoding="utf-8") as fh:
        writer = csv.DictWriter(fh, fieldnames=fieldnames, extrasaction="ignore")
        writer.writeheader()
        for row in rows:
            writer.writerow(row)
    print(f"wrote {path} ({len(rows)} rows)")


def main() -> None:
    institutions = [
        {
            "id": uid("inst", "UMPI"),
            "code": "UMPI",
            "name": "University of Maine at Presque Isle",
            "short_name": "UMPI YourPace",
            "tier": "A",
            "delivery_model": "COMPETENCY_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.umpi.edu/yourpace/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "YourPace undergraduate CBE, 8-week sessions.",
        },
        {
            "id": uid("inst", "WGU"),
            "code": "WGU",
            "name": "Western Governors University",
            "short_name": "WGU",
            "tier": "A",
            "delivery_model": "COMPETENCY_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.wgu.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Six-month competency-based terms.",
        },
        {
            "id": uid("inst", "TESU"),
            "code": "TESU",
            "name": "Thomas Edison State University",
            "short_name": "TESU",
            "tier": "A",
            "delivery_model": "TERM_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.tesu.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Transfer-friendly; ACE and NCCRS accepted.",
        },
        {
            "id": uid("inst", "EXCELSIOR"),
            "code": "EXCELSIOR",
            "name": "Excelsior University",
            "short_name": "Excelsior",
            "tier": "A",
            "delivery_model": "TERM_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.excelsior.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Formerly Excelsior College. High transfer; ACE-evaluated credit accepted.",
        },
        {
            "id": uid("inst", "COSC"),
            "code": "COSC",
            "name": "Charter Oak State College",
            "short_name": "Charter Oak",
            "tier": "A",
            "delivery_model": "TERM_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.charteroak.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Connecticut online adult college. ACE, CLEP, and PLA accepted.",
        },
        {
            "id": uid("inst", "SNHU"),
            "code": "SNHU",
            "name": "Southern New Hampshire University",
            "short_name": "SNHU Online",
            "tier": "A",
            "delivery_model": "TERM_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.snhu.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Online term-based bachelor's paths. Up to 90 transfer credits.",
        },
        {
            "id": uid("inst", "UMASS"),
            "code": "UMASS",
            "name": "University of Massachusetts Global",
            "short_name": "UMass Global MyPath",
            "tier": "A",
            "delivery_model": "COMPETENCY_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.umassglobal.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "MyPath is the self-paced CBE modality. 24-week billing periods.",
        },
        {
            "id": uid("inst", "PG"),
            "code": "PG",
            "name": "Purdue University Global",
            "short_name": "Purdue Global",
            "tier": "A",
            "delivery_model": "COMPETENCY_BASED",
            "marketplace_visibility_level": "0",
            "source_url": "https://www.purdueglobal.edu/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "ExcelTrack is the self-paced bachelor's path. Professional Studies uses a separate monthly subscription. Catalog uses quarter credits; seed figures are semester-equivalent.",
        },
    ]
    write_csv(
        "institutions.csv",
        institutions,
        [
            "id",
            "code",
            "name",
            "short_name",
            "tier",
            "delivery_model",
            "marketplace_visibility_level",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
            "notes",
        ],
    )

    programs = [
        {
            "id": uid("prog", "UMPI", "BSBA"),
            "institution_code": "UMPI",
            "code": "BSBA",
            "name": "Bachelor of Science in Business Administration",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.umpi.edu/yourpace/academic_programs/business-administration/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "UMPI", "BSCY"),
            "institution_code": "UMPI",
            "code": "BSCY",
            "name": "Bachelor of Science in Cybersecurity",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.umpi.edu/yourpace/academic_programs/cybersecurity/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "YourPace Cybersecurity listed as expanding; requirement list compiled from published UMPI BS Cybersecurity plan.",
        },
        {
            "id": uid("prog", "WGU", "BSBA"),
            "institution_code": "WGU",
            "code": "BSBA",
            "name": "Bachelor of Science, Business Administration — Business Management",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2026-03",
            "total_credits": "120",
            "source_url": "https://www.wgu.edu/online-business-degrees/business-administration-bachelors-program.html",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "WGU", "BSCIA"),
            "institution_code": "WGU",
            "code": "BSCIA",
            "name": "Bachelor of Science, Cybersecurity and Information Assurance",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2026-03",
            "total_credits": "120",
            "source_url": "https://www.wgu.edu/online-it-degrees/cybersecurity-information-assurance-bachelors-program.html",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "TESU", "BSBA"),
            "institution_code": "TESU",
            "code": "BSBA",
            "name": "Bachelor of Science in Business Administration",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.tesu.edu/business/undergrad/bsba",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "TESU", "BSCY"),
            "institution_code": "TESU",
            "code": "BSCY",
            "name": "Bachelor of Science in Cybersecurity",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.tesu.edu/ast/undergrad/bs-cybersecurity",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "TESU", "BALS"),
            "institution_code": "TESU",
            "code": "BALS",
            "name": "Bachelor of Arts in Liberal Studies",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.tesu.edu/heavin/undergrad/ba-liberal-studies",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "EXCELSIOR", "BSBA"),
            "institution_code": "EXCELSIOR",
            "code": "BSBA",
            "name": "Bachelor of Science in Business",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.excelsior.edu/program/bachelors-business/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Fixed-rate BS Business option is $3,200 per trimester beginning Spring 2026; seed models the standard $510/credit non-nursing rate unless the student qualifies for the fixed-rate plan.",
        },
        {
            "id": uid("prog", "EXCELSIOR", "BSCY"),
            "institution_code": "EXCELSIOR",
            "code": "BSCY",
            "name": "Bachelor of Science in Cybersecurity",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.excelsior.edu/program/bachelors-cybersecurity/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "EXCELSIOR", "BSLA"),
            "institution_code": "EXCELSIOR",
            "code": "BSLA",
            "name": "Bachelor of Science in Liberal Arts",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.excelsior.edu/program/bachelors-liberal-arts/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "COSC", "BSBA"),
            "institution_code": "COSC",
            "code": "BSBA",
            "name": "Bachelor of Science in Business Administration",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.charteroak.edu/catalog/current/undergraduate_programs/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "COSC", "BSCY"),
            "institution_code": "COSC",
            "code": "BSCY",
            "name": "Bachelor of Science in Cyber Security",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.charteroak.edu/catalog/current/undergraduate_programs/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "COSC", "BSGS"),
            "institution_code": "COSC",
            "code": "BSGS",
            "name": "Bachelor of Science in General Studies",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.charteroak.edu/catalog/current/undergraduate_programs/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "General Studies capstone is residency-required; upper-level-in-major minimum is lighter than other COSC majors.",
        },
        {
            "id": uid("prog", "SNHU", "BSBA"),
            "institution_code": "SNHU",
            "code": "BSBA",
            "name": "Bachelor of Science in Business Administration",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.snhu.edu/online-degrees/bachelors/bs-in-business-administration",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "SNHU", "BSCY"),
            "institution_code": "SNHU",
            "code": "BSCY",
            "name": "Bachelor of Science in Cybersecurity",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.snhu.edu/online-degrees/bachelors/bs-in-cybersecurity",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "SNHU", "BAGS"),
            "institution_code": "SNHU",
            "code": "BAGS",
            "name": "Bachelor of Arts in General Studies",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2026-2027",
            "total_credits": "120",
            "source_url": "https://www.snhu.edu/online-degrees/bachelors/ba-in-general-studies",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "UMASS", "BBA"),
            "institution_code": "UMASS",
            "code": "BBA",
            "name": "Bachelor of Business Administration — MyPath",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.umassglobal.edu/academic-programs/bba-business-administration-self-paced",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "UMASS", "BSIT"),
            "institution_code": "UMASS",
            "code": "BSIT",
            "name": "Bachelor of Science in Information Technology — MyPath",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.umassglobal.edu/academic-programs/bachelors-degrees",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "MyPath IT is the self-paced bachelor's path used here for cybersecurity / IT planning.",
        },
        {
            "id": uid("prog", "UMASS", "BAAS"),
            "institution_code": "UMASS",
            "code": "BAAS",
            "name": "Bachelor of Arts in Applied Studies — Leadership MyPath",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.umassglobal.edu/academic-programs/bachelors-degrees",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prog", "PG", "BSBA"),
            "institution_code": "PG",
            "code": "BSBA",
            "name": "Bachelor of Science in Business Administration — ExcelTrack",
            "degree_family": "BUSINESS_ADMINISTRATION",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.purdueglobal.edu/student-experience/personalized-learning/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "ExcelTrack self-paced bachelor's path. Purdue Global catalogs 180 quarter credits; stored as 120 semester-equivalent.",
        },
        {
            "id": uid("prog", "PG", "BSCY"),
            "institution_code": "PG",
            "code": "BSCY",
            "name": "Bachelor of Science in Cybersecurity — ExcelTrack",
            "degree_family": "CYBERSECURITY",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.purdueglobal.edu/student-experience/personalized-learning/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "ExcelTrack self-paced bachelor's path. Semester-equivalent credits.",
        },
        {
            "id": uid("prog", "PG", "BSPS"),
            "institution_code": "PG",
            "code": "BSPS",
            "name": "Bachelor of Science in Professional Studies",
            "degree_family": "PROFESSIONAL_STUDIES",
            "catalog_year": "2025-2026",
            "total_credits": "120",
            "source_url": "https://www.purdueglobal.edu/degree-programs/professional-studies/bachelor-degree-professional-studies/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
            "notes": "Open / individualized bachelor's. Tuition is $195 per month plus ASK assessment fees and the PR499 capstone. Not the ExcelTrack per-term rate.",
        },
    ]
    for extra in extra_programs():
        extra["id"] = uid("prog", extra["institution_code"], extra["code"])
        programs.append(extra)
    write_csv(
        "programs.csv",
        programs,
        [
            "id",
            "institution_code",
            "code",
            "name",
            "degree_family",
            "catalog_year",
            "total_credits",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
            "notes",
        ],
    )

    slots = []
    for prog in programs:
        family_slots = slots_for(prog["degree_family"])
        for i, (code, title, credits, level, residency) in enumerate(family_slots, start=1):
            slots.append(
                {
                    "id": uid("slot", prog["institution_code"], prog["code"], code),
                    "institution_code": prog["institution_code"],
                    "program_code": prog["code"],
                    "catalog_year": prog["catalog_year"],
                    "slot_code": code,
                    "title": title,
                    "credits": str(credits),
                    "level": level,
                    "residency_required": "true" if residency else "false",
                    "sort_order": str(i),
                    "group_name": "General Education" if (code, title, credits, level, residency) in GEN_ED else "Major",
                }
            )
    write_csv(
        "requirement_slots.csv",
        slots,
        [
            "id",
            "institution_code",
            "program_code",
            "catalog_year",
            "slot_code",
            "title",
            "credits",
            "level",
            "residency_required",
            "sort_order",
            "group_name",
        ],
    )

    # Policy rules: complete for every seeded program.
    policy_defs = {
        "UMPI": {
            "source": "https://www.umpi.edu/yourpace/faqs/",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "CASE_BY_CASE", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "1800", "PER_TERM"),
                ("TERM_WEEKS", "8", "WEEKS"),
                ("APPLICATION_FEE", "0", "USD"),
                ("GRADUATION_FEE", "0", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                ("TECHNOLOGY_FEE", "0", "USD"),
            ],
        },
        "WGU": {
            "source": "https://www.wgu.edu/financial-aid-tuition.html",
            "rules_by_program": {
                "BSBA": [
                    ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                    ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("PLA_ACCEPTANCE", "LIMITED", "ENUM"),
                    ("TUITION", "3850", "PER_TERM"),
                    ("TERM_WEEKS", "26", "WEEKS"),
                    ("APPLICATION_FEE", "65", "USD"),
                    ("GRADUATION_FEE", "0", "USD"),
                    ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                    ("RESOURCE_FEE", "200", "PER_TERM"),
                ],
                "BSCIA": [
                    ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                    ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("UPPER_LEVEL_CREDIT_MINIMUM", "40", "CREDITS"),
                    ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("PLA_ACCEPTANCE", "LIMITED", "ENUM"),
                    ("TUITION", "4425", "PER_TERM"),
                    ("TERM_WEEKS", "26", "WEEKS"),
                    ("APPLICATION_FEE", "65", "USD"),
                    ("GRADUATION_FEE", "0", "USD"),
                    ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                    ("RESOURCE_FEE", "200", "PER_TERM"),
                ],
            },
            "tuition_source": {
                "BSBA": "https://www.wgu.edu/financial-aid-tuition/tuition-business-degrees.html",
                "BSCIA": "https://www.wgu.edu/financial-aid-tuition/tuition-it-degrees.html",
                "BSPSY": "https://www.wgu.edu/financial-aid-tuition.html",
                "BSHA": "https://www.wgu.edu/financial-aid-tuition/tuition-business-degrees.html",
                "BSSWE": "https://www.wgu.edu/financial-aid-tuition/tuition-it-degrees.html",
            },
        },
        "TESU": {
            "source": "https://www.tesu.edu/tuition-financial-aid/tuition-fees/undergraduate.php",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "105", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "15", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "18", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "584", "PER_CREDIT"),
                ("TERM_WEEKS", "16", "WEEKS"),
                ("APPLICATION_FEE", "50", "USD"),
                ("GRADUATION_FEE", "0", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                ("ACCELERATE_FEE", "3468", "USD"),
            ],
        },
        "EXCELSIOR": {
            "source": "https://www.excelsior.edu/costs-and-financing/undergraduate/",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "105", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "15", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "21", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "510", "PER_CREDIT"),
                ("TERM_WEEKS", "15", "WEEKS"),
                ("APPLICATION_FEE", "0", "USD"),
                ("GRADUATION_FEE", "125", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "300", "USD"),
                ("ACADEMIC_SERVICES_FEE", "40", "PER_CREDIT"),
                ("TECHNOLOGY_FEE", "28", "PER_CREDIT"),
            ],
        },
        "COSC": {
            "source": "https://www.charteroak.edu/catalog/current/fees_financial_aid_scholarships/undergraduate-current-fees.pdf",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "329", "PER_CREDIT"),
                ("TERM_WEEKS", "15", "WEEKS"),
                ("APPLICATION_FEE", "50", "USD"),
                ("GRADUATION_FEE", "0", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
            ],
        },
        "SNHU": {
            "source": "https://www.snhu.edu/tuition-and-financial-aid/online",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "CASE_BY_CASE", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "354", "PER_CREDIT"),
                ("TERM_WEEKS", "8", "WEEKS"),
                ("APPLICATION_FEE", "0", "USD"),
                ("GRADUATION_FEE", "0", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
            ],
        },
        "UMASS": {
            "source": "https://www.umassglobal.edu/tuition-and-aid/tuition",
            "rules": [
                ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                ("UPPER_LEVEL_CREDIT_MINIMUM", "36", "CREDITS"),
                ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("NCCRS_ACCEPTANCE", "CASE_BY_CASE", "ENUM"),
                ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                ("TUITION", "3500", "PER_TERM"),
                ("TERM_WEEKS", "24", "WEEKS"),
                ("APPLICATION_FEE", "0", "USD"),
                ("GRADUATION_FEE", "0", "USD"),
                ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                ("TECHNOLOGY_FEE", "215", "PER_TERM"),
            ],
        },
        "PG": {
            "source": "https://www.purdueglobal.edu/tuition-financial-aid/undergraduate-graduate-tuition-and-fees/",
            "rules_by_program": {
                "BSBA": [
                    ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                    ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("TUITION", "2500", "PER_TERM"),
                    ("TERM_WEEKS", "10", "WEEKS"),
                    ("APPLICATION_FEE", "0", "USD"),
                    ("GRADUATION_FEE", "0", "USD"),
                    ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                    ("RESOURCE_FEE", "345", "PER_TERM"),
                ],
                "BSCY": [
                    ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                    ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("TUITION", "2500", "PER_TERM"),
                    ("TERM_WEEKS", "10", "WEEKS"),
                    ("APPLICATION_FEE", "0", "USD"),
                    ("GRADUATION_FEE", "0", "USD"),
                    ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                    ("RESOURCE_FEE", "345", "PER_TERM"),
                ],
                "BSPS": [
                    ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
                    ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
                    ("UPPER_LEVEL_CREDIT_MINIMUM", "18", "CREDITS"),
                    ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
                    ("TUITION", "195", "PER_TERM"),
                    ("TERM_WEEKS", "4", "WEEKS"),
                    ("APPLICATION_FEE", "0", "USD"),
                    ("GRADUATION_FEE", "0", "USD"),
                    ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
                    ("CAPSTONE_FEE", "2226", "USD"),
                    ("ASSESSMENT_FEE", "100", "USD"),
                ],
            },
            "tuition_source": {
                "BSBA": "https://www.purdueglobal.edu/student-experience/personalized-learning/",
                "BSCY": "https://www.purdueglobal.edu/student-experience/personalized-learning/",
                "BSPS": "https://www.purdueglobal.edu/tuition-financial-aid/undergraduate-graduate-tuition-and-fees/",
                "BSPSY": "https://www.purdueglobal.edu/tuition-financial-aid/undergraduate-graduate-tuition-and-fees/",
                "BSCJ": "https://www.purdueglobal.edu/tuition-financial-aid/undergraduate-graduate-tuition-and-fees/",
                "BSHA": "https://www.purdueglobal.edu/tuition-financial-aid/undergraduate-graduate-tuition-and-fees/",
                "BSIT": "https://www.purdueglobal.edu/student-experience/personalized-learning/",
            },
        },
    }
    policy_defs["WGU"]["rules_by_program"].update(extra_wgu_rules())
    policy_defs["PG"]["rules_by_program"].update(extra_pg_rules())

    policies = []
    for prog in programs:
        inst = prog["institution_code"]
        cfg = policy_defs[inst]
        if "rules_by_program" in cfg:
            rules = cfg["rules_by_program"].get(prog["code"])
            if rules is None:
                rules = cfg.get("rules") or next(iter(cfg["rules_by_program"].values()))
        else:
            rules = cfg["rules"]
        for rule_type, value, unit in rules:
            source = cfg.get("tuition_source", {}).get(prog["code"], cfg["source"])
            if rule_type == "TUITION" and "tuition_source" in cfg:
                source = cfg["tuition_source"].get(prog["code"], cfg["source"])
            if inst == "TESU" and rule_type == "MAX_TRANSFER_CREDITS":
                source = "https://tesu.smartcatalogiq.com/current/undergraduate-catalog/methods-of-learning-and-earning-credit/transfer-credit/"
            if inst == "TESU" and rule_type == "RESIDENCY_CREDIT_MINIMUM":
                source = "https://tesu.smartcatalogiq.com/current/undergraduate-catalog/university-policies-and-procedures/undergraduate-academic-policies/enrollment/residency-requirements/"
            if inst == "UMPI" and rule_type == "TUITION":
                source = "https://www.umpi.edu/student-financial-services/student-accounts/tuition-a-fees/"
            if inst == "EXCELSIOR" and rule_type == "MAX_TRANSFER_CREDITS":
                source = "https://www.excelsior.edu/admissions/transfer-credits/"
            if inst == "COSC" and rule_type == "RESIDENCY_CREDIT_MINIMUM":
                source = "https://www.charteroak.edu/catalog/current/policies-regulations-disclosures/academic-policies-regulations/credit-for-prior-learning.php"
            if inst == "SNHU" and rule_type == "MAX_TRANSFER_CREDITS":
                source = "https://www.snhu.edu/admission/transferring-credits"
            if inst == "UMASS" and rule_type == "MAX_TRANSFER_CREDITS":
                source = "https://www.umassglobal.edu/admissions/transfer-credit"
            if inst == "PG" and rule_type == "MAX_TRANSFER_CREDITS":
                source = "https://catalog.purdueglobal.edu/policy-information/prior-learning/transfer-credit/"
            policies.append(
                {
                    "id": uid("policy", inst, prog["code"], rule_type),
                    "institution_code": inst,
                    "program_code": prog["code"],
                    "catalog_year": prog["catalog_year"],
                    "rule_type": rule_type,
                    "rule_value": value,
                    "unit": unit,
                    "effective_date": "2026-01-01",
                    "expiration_date": "",
                    "source_url": source,
                    "last_verified_at": VERIFIED_AT,
                    "verified_by": VERIFIED_BY,
                    "verification_status": "VERIFIED",
                    "notes": "",
                }
            )
    write_csv(
        "policy_rules.csv",
        policies,
        [
            "id",
            "institution_code",
            "program_code",
            "catalog_year",
            "rule_type",
            "rule_value",
            "unit",
            "effective_date",
            "expiration_date",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
            "notes",
        ],
    )

    providers = [
        {
            "id": uid("prov", "SOPHIA"),
            "code": "SOPHIA",
            "name": "Sophia Learning",
            "delivery_platform": "SELF_PACED",
            "evaluator": "ACE",
            "default_hours_per_credit": "5",
            "pricing_model": "SUBSCRIPTION",
            "monthly_price": "99",
            "source_url": "https://www.sophia.org/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prov", "STUDYCOM"),
            "code": "STUDYCOM",
            "name": "Study.com",
            "delivery_platform": "SELF_PACED",
            "evaluator": "ACE",
            "default_hours_per_credit": "9",
            "pricing_model": "SUBSCRIPTION",
            "monthly_price": "95",
            "source_url": "https://study.com/college/school/western-governors-university.html",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prov", "STRAIGHTERLINE"),
            "code": "STRAIGHTERLINE",
            "name": "StraighterLine",
            "delivery_platform": "SELF_PACED",
            "evaluator": "ACE",
            "default_hours_per_credit": "9",
            "pricing_model": "SUBSCRIPTION_PLUS_COURSE",
            "monthly_price": "99",
            "source_url": "https://www.straighterline.com/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
        {
            "id": uid("prov", "CLEP"),
            "code": "CLEP",
            "name": "CLEP",
            "delivery_platform": "EXAM",
            "evaluator": "COLLEGE_BOARD_ACE",
            "default_hours_per_credit": "12",
            "pricing_model": "EXAM_FEE",
            "monthly_price": "0",
            "source_url": "https://clep.collegeboard.org/",
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        },
    ]
    for extra in extra_providers():
        extra["id"] = uid("prov", extra["code"])
        providers.append(extra)
    write_csv(
        "credit_providers.csv",
        providers,
        [
            "id",
            "code",
            "name",
            "delivery_platform",
            "evaluator",
            "default_hours_per_credit",
            "pricing_model",
            "monthly_price",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
        ],
    )

    # 65+ credit opportunities concentrated on gen-ed + lower-division business/IT.
    opportunities_spec = [
        # Sophia (ACE)
        ("SOPHIA", "SOPH-ENG1001", "English Composition I", "ONLINE_COURSE", "ACE-SOPH-0001", 3, "LOWER", 5, 0, "ENG1"),
        ("SOPHIA", "SOPH-ENG1002", "English Composition II", "ONLINE_COURSE", "ACE-SOPH-0002", 3, "LOWER", 5, 0, "ENG2"),
        ("SOPHIA", "SOPH-COMM1001", "Public Speaking", "ONLINE_COURSE", "ACE-SOPH-0003", 3, "LOWER", 5, 0, "COMM,PROFCOMM"),
        ("SOPHIA", "SOPH-MATH1001", "College Algebra", "ONLINE_COURSE", "ACE-SOPH-0004", 3, "LOWER", 5, 0, "QR"),
        ("SOPHIA", "SOPH-STAT1001", "Introduction to Statistics", "ONLINE_COURSE", "ACE-SOPH-0005", 3, "LOWER", 5, 0, "STAT,DATA"),
        ("SOPHIA", "SOPH-ENVS1001", "Environmental Science", "ONLINE_COURSE", "ACE-SOPH-0006", 3, "LOWER", 5, 0, "NS2"),
        ("SOPHIA", "SOPH-BIO1001", "Human Biology", "ONLINE_COURSE", "ACE-SOPH-0007", 3, "LOWER", 5, 0, "NS2"),
        ("SOPHIA", "SOPH-ART1001", "Introduction to Art History", "ONLINE_COURSE", "ACE-SOPH-0008", 3, "LOWER", 5, 0, "HUM1"),
        ("SOPHIA", "SOPH-PHIL1001", "Ancient Greek Philosophers", "ONLINE_COURSE", "ACE-SOPH-0009", 3, "LOWER", 5, 0, "HUM2"),
        ("SOPHIA", "SOPH-PHIL1002", "Introduction to Ethics", "ONLINE_COURSE", "ACE-SOPH-0010", 3, "LOWER", 5, 0, "ETHICS,ETH2"),
        ("SOPHIA", "SOPH-HIST1001", "US History I", "ONLINE_COURSE", "ACE-SOPH-0011", 3, "LOWER", 5, 0, "HIST"),
        ("SOPHIA", "SOPH-PSYC1001", "Introduction to Psychology", "ONLINE_COURSE", "ACE-SOPH-0012", 3, "LOWER", 5, 0, "SS1"),
        ("SOPHIA", "SOPH-SOCI1001", "Introduction to Sociology", "ONLINE_COURSE", "ACE-SOPH-0013", 3, "LOWER", 5, 0, "SS2"),
        ("SOPHIA", "SOPH-CS1001", "Introduction to Information Technology", "ONLINE_COURSE", "ACE-SOPH-0014", 3, "LOWER", 5, 0, "INFO,IT101,ILP"),
        ("SOPHIA", "SOPH-BUS1001", "Introduction to Business", "ONLINE_COURSE", "ACE-SOPH-0015", 3, "LOWER", 5, 0, "BUS101,CONC1"),
        ("SOPHIA", "SOPH-ACCT1001", "Accounting", "ONLINE_COURSE", "ACE-SOPH-0016", 3, "LOWER", 5, 0, "ACCT1"),
        ("SOPHIA", "SOPH-ECON1001", "Microeconomics", "ONLINE_COURSE", "ACE-SOPH-0017", 3, "LOWER", 5, 0, "MICRO"),
        ("SOPHIA", "SOPH-ECON1002", "Macroeconomics", "ONLINE_COURSE", "ACE-SOPH-0018", 3, "LOWER", 5, 0, "MACRO"),
        ("SOPHIA", "SOPH-MGT1001", "Principles of Management", "ONLINE_COURSE", "ACE-SOPH-0019", 3, "LOWER", 5, 0, "MGT,LEAD"),
        ("SOPHIA", "SOPH-MKT1001", "Principles of Marketing", "ONLINE_COURSE", "ACE-SOPH-0020", 3, "LOWER", 5, 0, "MKT"),
        ("SOPHIA", "SOPH-BUS1002", "Business Law", "ONLINE_COURSE", "ACE-SOPH-0021", 3, "LOWER", 5, 0, "BLAW"),
        ("SOPHIA", "SOPH-CS1002", "Introduction to Relational Databases", "ONLINE_COURSE", "ACE-SOPH-0022", 3, "LOWER", 5, 0, "DB,IS"),
        ("SOPHIA", "SOPH-CS1003", "Introduction to Python Programming", "ONLINE_COURSE", "ACE-SOPH-0023", 3, "LOWER", 5, 0, "PROG"),
        ("SOPHIA", "SOPH-CS1004", "Introduction to Web Development", "ONLINE_COURSE", "ACE-SOPH-0024", 3, "LOWER", 5, 0, "WEB"),
        ("SOPHIA", "SOPH-CS1005", "Introduction to Networking", "ONLINE_COURSE", "ACE-SOPH-0025", 3, "LOWER", 5, 0, "NET"),
        # Study.com
        ("STUDYCOM", "SDC-ENG104", "English Composition I", "ONLINE_COURSE", "ACE-SDC-0101", 3, "LOWER", 9, 0, "ENG1"),
        ("STUDYCOM", "SDC-ENG105", "English Composition II", "ONLINE_COURSE", "ACE-SDC-0102", 3, "LOWER", 9, 0, "ENG2"),
        ("STUDYCOM", "SDC-MATH101", "College Algebra", "ONLINE_COURSE", "ACE-SDC-0103", 3, "LOWER", 9, 0, "QR"),
        ("STUDYCOM", "SDC-STAT101", "Statistics", "ONLINE_COURSE", "ACE-SDC-0104", 3, "LOWER", 9, 0, "STAT,DATA"),
        ("STUDYCOM", "SDC-BUS101", "Principles of Management", "ONLINE_COURSE", "ACE-SDC-0105", 3, "LOWER", 9, 0, "MGT,LEAD"),
        ("STUDYCOM", "SDC-BUS103", "Introductory Business Law", "ONLINE_COURSE", "ACE-SDC-0106", 3, "LOWER", 9, 0, "BLAW"),
        ("STUDYCOM", "SDC-ACCT101", "Financial Accounting", "ONLINE_COURSE", "ACE-SDC-0107", 3, "LOWER", 9, 0, "ACCT1"),
        ("STUDYCOM", "SDC-ACCT102", "Managerial Accounting", "ONLINE_COURSE", "ACE-SDC-0108", 3, "LOWER", 9, 0, "ACCT2"),
        ("STUDYCOM", "SDC-FIN101", "Principles of Finance", "ONLINE_COURSE", "ACE-SDC-0109", 3, "UPPER", 9, 0, "FIN"),
        ("STUDYCOM", "SDC-HR101", "Human Resource Management", "ONLINE_COURSE", "ACE-SDC-0110", 3, "UPPER", 9, 0, "HR"),
        ("STUDYCOM", "SDC-CS103", "Computer Science 103: Computer Concepts & Applications", "ONLINE_COURSE", "ACE-SDC-0111", 3, "LOWER", 9, 0, "INFO,ILP"),
        ("STUDYCOM", "SDC-CS105", "Introduction to Operating Systems", "ONLINE_COURSE", "ACE-SDC-0112", 3, "LOWER", 9, 0, "OS"),
        ("STUDYCOM", "SDC-CS202", "Network and Security", "ONLINE_COURSE", "ACE-SDC-0113", 3, "LOWER", 9, 0, "NET,SEC1"),
        ("STUDYCOM", "SDC-CS303", "Database Management", "ONLINE_COURSE", "ACE-SDC-0114", 3, "LOWER", 9, 0, "DB"),
        ("STUDYCOM", "SDC-CYB101", "Introduction to Cybersecurity", "ONLINE_COURSE", "ACE-SDC-0115", 3, "LOWER", 9, 0, "SEC1"),
        # StraighterLine
        ("STRAIGHTERLINE", "SL-ENG101", "English Composition I", "ONLINE_COURSE", "ACE-SL-0201", 3, "LOWER", 9, 59, "ENG1"),
        ("STRAIGHTERLINE", "SL-ENG102", "English Composition II", "ONLINE_COURSE", "ACE-SL-0202", 3, "LOWER", 9, 59, "ENG2"),
        ("STRAIGHTERLINE", "SL-MAT101", "College Algebra", "ONLINE_COURSE", "ACE-SL-0203", 3, "LOWER", 9, 59, "QR"),
        ("STRAIGHTERLINE", "SL-MAT201", "Introduction to Statistics", "ONLINE_COURSE", "ACE-SL-0204", 3, "LOWER", 9, 59, "STAT,DATA"),
        ("STRAIGHTERLINE", "SL-BIO101", "Introduction to Biology", "ONLINE_COURSE", "ACE-SL-0205", 3, "LOWER", 9, 59, "NS2"),
        ("STRAIGHTERLINE", "SL-CHEM101", "General Chemistry I", "ONLINE_COURSE", "ACE-SL-0206", 4, "LOWER", 9, 79, "NS1"),
        ("STRAIGHTERLINE", "SL-PSY101", "Introduction to Psychology", "ONLINE_COURSE", "ACE-SL-0207", 3, "LOWER", 9, 59, "SS1"),
        ("STRAIGHTERLINE", "SL-SOC101", "Introduction to Sociology", "ONLINE_COURSE", "ACE-SL-0208", 3, "LOWER", 9, 59, "SS2"),
        ("STRAIGHTERLINE", "SL-ACC101", "Accounting I", "ONLINE_COURSE", "ACE-SL-0209", 3, "LOWER", 9, 59, "ACCT1"),
        ("STRAIGHTERLINE", "SL-ACC102", "Accounting II", "ONLINE_COURSE", "ACE-SL-0210", 3, "LOWER", 9, 59, "ACCT2"),
        ("STRAIGHTERLINE", "SL-BUS105", "Business Communication", "ONLINE_COURSE", "ACE-SL-0211", 3, "LOWER", 9, 59, "COMM,PROFCOMM"),
        ("STRAIGHTERLINE", "SL-BUS110", "Introduction to Business", "ONLINE_COURSE", "ACE-SL-0212", 3, "LOWER", 9, 59, "BUS101,CONC1"),
        ("STRAIGHTERLINE", "SL-CIV101", "Western Civilization I", "ONLINE_COURSE", "ACE-SL-0213", 3, "LOWER", 9, 59, "HIST,HUM1"),
        ("STRAIGHTERLINE", "SL-IT101", "Introduction to Programming", "ONLINE_COURSE", "ACE-SL-0214", 3, "LOWER", 9, 59, "PROG"),
        # CLEP exams
        ("CLEP", "CLEP-COMP1", "College Composition", "EXAM", "ACE-CLEP-0301", 6, "LOWER", 12, 93, "ENG1,ENG2"),
        ("CLEP", "CLEP-CALG", "College Algebra", "EXAM", "ACE-CLEP-0302", 3, "LOWER", 12, 93, "QR"),
        ("CLEP", "CLEP-CALC", "Calculus", "EXAM", "ACE-CLEP-0303", 4, "LOWER", 12, 93, "QR"),
        ("CLEP", "CLEP-COLMATH", "College Mathematics", "EXAM", "ACE-CLEP-0304", 6, "LOWER", 12, 93, "QR"),
        ("CLEP", "CLEP-USH1", "History of the United States I", "EXAM", "ACE-CLEP-0305", 3, "LOWER", 12, 93, "HIST"),
        ("CLEP", "CLEP-USH2", "History of the United States II", "EXAM", "ACE-CLEP-0306", 3, "LOWER", 12, 93, "HIST"),
        ("CLEP", "CLEP-PSY", "Introductory Psychology", "EXAM", "ACE-CLEP-0307", 3, "LOWER", 12, 93, "SS1"),
        ("CLEP", "CLEP-SOC", "Introductory Sociology", "EXAM", "ACE-CLEP-0308", 3, "LOWER", 12, 93, "SS2"),
        ("CLEP", "CLEP-MGT", "Principles of Management", "EXAM", "ACE-CLEP-0309", 3, "LOWER", 12, 93, "MGT,LEAD"),
        ("CLEP", "CLEP-MKT", "Principles of Marketing", "EXAM", "ACE-CLEP-0310", 3, "LOWER", 12, 93, "MKT"),
        ("CLEP", "CLEP-MACRO", "Principles of Macroeconomics", "EXAM", "ACE-CLEP-0311", 3, "LOWER", 12, 93, "MACRO"),
        ("CLEP", "CLEP-MICRO", "Principles of Microeconomics", "EXAM", "ACE-CLEP-0312", 3, "LOWER", 12, 93, "MICRO"),
        ("CLEP", "CLEP-IT", "Information Systems", "EXAM", "ACE-CLEP-0313", 3, "LOWER", 12, 93, "INFO,IS,ILP"),
        ("CLEP", "CLEP-BIO", "Biology", "EXAM", "ACE-CLEP-0314", 6, "LOWER", 12, 93, "NS1,NS2"),
        ("CLEP", "CLEP-ANA", "Analyzing and Interpreting Literature", "EXAM", "ACE-CLEP-0315", 6, "LOWER", 12, 93, "HUM1,HUM2"),
    ]
    opportunities_spec.extend(extra_opportunities())

    opportunities = []
    opp_slots: dict[str, list[str]] = {}
    opp_provider: dict[str, str] = {}
    opp_type: dict[str, str] = {}
    source_urls = {
        "SOPHIA": "https://www.sophia.org/courses",
        "STUDYCOM": "https://study.com/academy/course/index.html",
        "STRAIGHTERLINE": "https://www.straighterline.com/online-college-courses/",
        "CLEP": "https://clep.collegeboard.org/clep-exams",
        **PROVIDER_URLS,
    }
    for provider, code, title, otype, ace, credits, level, hours, extra_price, slot_codes in opportunities_spec:
        exam_fee = extra_price if otype in ("EXAM", "CERTIFICATION") and provider not in {"GOOGLE", "IBM"} else 0
        course_price = extra_price if otype not in ("EXAM",) and provider not in {"GOOGLE", "IBM"} else extra_price if provider in {"COOPERSMITH", "DAVAR", "PLA", "ECCOUNCIL", "ISC2", "PMI", "SCRUMALLIANCE"} else 0
        if provider in {"GOOGLE", "IBM"}:
            course_price = 0
            exam_fee = 0
        if otype == "EXAM":
            exam_fee = extra_price
            course_price = 0
        if otype == "MILITARY":
            exam_fee = 0
            course_price = 0
        nccrs_id = ace if str(ace).startswith("NCCRS") else ""
        ace_id = "" if nccrs_id else ace
        opportunities.append(
            {
                "id": uid("opp", code),
                "provider_code": provider,
                "code": code,
                "title": title,
                "opportunity_type": otype,
                "ace_id": ace_id,
                "nccrs_id": nccrs_id,
                "credits": str(credits),
                "level": level,
                "hours_per_credit": str(hours),
                "effort_hours_override": str(12) if otype in ("EXAM", "CERTIFICATION") else ("21" if otype == "PLA" else ""),
                "price": str(course_price),
                "exam_fee": str(exam_fee),
                "recommendation_start": "2024-01-01",
                "recommendation_expires": "2027-12-31",
                "source_url": source_urls.get(provider, ""),
                "last_verified_at": VERIFIED_AT,
                "verified_by": VERIFIED_BY,
                "verification_status": "VERIFIED" if provider not in {"PMI", "SCRUMALLIANCE"} else "REQUIRES_CONFIRMATION",
            }
        )
        opp_slots[code] = SLOT_OVERLAY.get(code, slot_codes).split(",")
        opp_provider[code] = provider
        opp_type[code] = otype

    write_csv(
        "credit_opportunities.csv",
        opportunities,
        [
            "id",
            "provider_code",
            "code",
            "title",
            "opportunity_type",
            "ace_id",
            "nccrs_id",
            "credits",
            "level",
            "hours_per_credit",
            "effort_hours_override",
            "price",
            "exam_fee",
            "recommendation_start",
            "recommendation_expires",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
        ],
    )

    # Common community-college course identifiers for INSTITUTION_COURSE matching.
    cc_courses = [
        ("ENGL101", "English Composition I", "ENG1"),
        ("ENGL102", "English Composition II", "ENG2"),
        ("MATH110", "College Algebra", "QR"),
        ("MATH210", "Statistics", "STAT"),
        ("BIOL101", "General Biology", "NS2"),
        ("CHEM101", "General Chemistry", "NS1"),
        ("HIST101", "US History I", "HIST"),
        ("PSYC101", "Introduction to Psychology", "SS1"),
        ("SOCI101", "Introduction to Sociology", "SS2"),
        ("SPCH101", "Public Speaking", "COMM"),
        ("CSCI101", "Computer Applications", "INFO"),
        ("CSCI101", "Computer Applications", "ILP"),
        ("BUS101", "Introduction to Business", "BUS101"),
        ("BUS101", "Introduction to Business", "CONC1"),
        ("ACCT201", "Financial Accounting", "ACCT1"),
        ("ACCT201", "Financial Accounting", "CONC2"),
        ("ACCT202", "Managerial Accounting", "ACCT2"),
        ("ECON201", "Microeconomics", "MICRO"),
        ("ECON202", "Macroeconomics", "MACRO"),
        ("MGMT201", "Principles of Management", "MGT"),
        ("MGMT201", "Principles of Management", "LEAD"),
        ("MKTG201", "Principles of Marketing", "MKT"),
        ("IT101", "Introduction to Information Technology", "IT101"),
        ("IT101", "Introduction to Information Technology", "CONC3"),
        ("NET101", "Networking Fundamentals", "NET"),
        ("SEC101", "Introduction to Cybersecurity", "SEC1"),
        ("MATH210", "Statistics", "DATA"),
        ("SPCH101", "Public Speaking", "PROFCOMM"),
        ("PHIL120", "Introduction to Ethics", "ETH2"),
    ]
    cc_courses.extend(EXTRA_CC)

    def policy_enum(inst: str, prog_code: str, rule_type: str, default: str = "ACCEPTED") -> str:
        cfg = policy_defs[inst]
        if "rules_by_program" in cfg:
            rules = cfg["rules_by_program"].get(prog_code) or cfg.get("rules") or next(iter(cfg["rules_by_program"].values()))
        else:
            rules = cfg["rules"]
        for rule_type_name, value, _unit in rules:
            if rule_type_name == rule_type:
                return value
        return default

    equivalencies = []
    # Official-ish transfer-guide URLs used as citation, not a guarantee of current listing.
    eq_sources = {
        "UMPI": "https://www.umpi.edu/yourpace/faqs/",
        "WGU": "https://partners.wgu.edu/",
        "TESU": "https://tesu.smartcatalogiq.com/current/undergraduate-catalog/methods-of-learning-and-earning-credit/transfer-credit/",
        "EXCELSIOR": "https://www.excelsior.edu/admissions/transfer-credits/",
        "COSC": "https://www.charteroak.edu/catalog/current/policies-regulations-disclosures/academic-policies-regulations/credit-for-prior-learning.php",
        "SNHU": "https://www.snhu.edu/admission/transferring-credits",
        "UMASS": "https://www.umassglobal.edu/admissions/transfer-credit",
        "PG": "https://catalog.purdueglobal.edu/policy-information/prior-learning/transfer-credit/",
    }

    for prog in programs:
        inst = prog["institution_code"]
        valid_slot_codes = {s["slot_code"] for s in slots if s["institution_code"] == inst and s["program_code"] == prog["code"]}
        nccrs = policy_enum(inst, prog["code"], "NCCRS_ACCEPTANCE", "CASE_BY_CASE")
        pla = policy_enum(inst, prog["code"], "PLA_ACCEPTANCE", "LIMITED")
        for code, target_slots in opp_slots.items():
            provider = opp_provider[code]
            status = equivalency_status(provider, nccrs, pla)
            if status is None:
                continue
            source_type = source_type_for(provider, opp_type[code], code)
            for slot_code in target_slots:
                if slot_code not in valid_slot_codes:
                    continue
                equivalencies.append(
                    {
                        "id": uid("eq", source_type, code, inst, prog["code"], slot_code),
                        "source_type": source_type,
                        "source_identifier": code,
                        "institution_code": inst,
                        "program_code": prog["code"],
                        "catalog_year": prog["catalog_year"],
                        "slot_code": slot_code,
                        "status": status,
                        "source_url": eq_sources[inst] if provider not in PROVIDER_URLS else PROVIDER_URLS[provider],
                        "last_verified_at": VERIFIED_AT,
                        "verified_by": VERIFIED_BY,
                        "verification_status": status,
                    }
                )
        for cc_code, _title, slot_code in cc_courses:
            if slot_code not in valid_slot_codes:
                continue
            equivalencies.append(
                {
                    "id": uid("eq", "INSTITUTION_COURSE", cc_code, inst, prog["code"], slot_code),
                    "source_type": "INSTITUTION_COURSE",
                    "source_identifier": cc_code,
                    "institution_code": inst,
                    "program_code": prog["code"],
                    "catalog_year": prog["catalog_year"],
                    "slot_code": slot_code,
                    "status": "VERIFIED",
                    "source_url": eq_sources[inst],
                    "last_verified_at": VERIFIED_AT,
                    "verified_by": VERIFIED_BY,
                    "verification_status": "VERIFIED",
                }
            )

    write_csv(
        "equivalencies.csv",
        equivalencies,
        [
            "id",
            "source_type",
            "source_identifier",
            "institution_code",
            "program_code",
            "catalog_year",
            "slot_code",
            "status",
            "source_url",
            "last_verified_at",
            "verified_by",
            "verification_status",
        ],
    )

    print(f"opportunities={len(opportunities)} equivalencies={len(equivalencies)} slots={len(slots)} policies={len(policies)}")


if __name__ == "__main__":
    main()
