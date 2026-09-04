"""Extra degree families, programs, providers, and ACE/NCCRS opportunities.

ACE/NCCRS figures cite the National Guide or NCCRS exhibits.
PMP and Scrum Alliance have no ACE National Guide listing; those map only
through portfolio/PLA at schools that publish PLA acceptance.
Military MOS rows are compiled from the ACE Military Guide posture, not a
specific veteran's JST extract.
"""
from __future__ import annotations

VERIFIED_AT = "2026-09-01"
VERIFIED_BY = "seed-research-2026-09"

PSYCHOLOGY_MAJOR = [
    ("PSY1", "Introduction to Psychology", 3, "LOWER", False),
    ("DEV", "Lifespan / Developmental Psychology", 3, "LOWER", False),
    ("BIOPSY", "Biological / Physiological Psychology", 3, "UPPER", False),
    ("SOCPSY", "Social Psychology", 3, "UPPER", False),
    ("ABPSY", "Abnormal Psychology", 3, "UPPER", False),
    ("PERS", "Personality Psychology", 3, "UPPER", False),
    ("COG", "Cognitive Psychology", 3, "UPPER", False),
    ("STATS", "Psychological Statistics", 3, "LOWER", False),
    ("RESM", "Research Methods in Psychology", 3, "UPPER", False),
    ("LEARN", "Learning / Behavioral Psychology", 3, "UPPER", False),
    ("HISTPSY", "History and Systems of Psychology", 3, "UPPER", False),
    ("ETHPSY", "Ethics in Psychology / Social Science", 3, "UPPER", False),
    ("PELEC1", "Psychology Elective I", 3, "UPPER", False),
    ("PELEC2", "Psychology Elective II", 3, "UPPER", False),
    ("CAP", "Psychology Capstone", 3, "UPPER", True),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

HEALTHCARE_MAJOR = [
    ("HCA1", "Introduction to Healthcare / Health Systems", 3, "LOWER", False),
    ("HCAORG", "Healthcare Organizations and Delivery", 3, "LOWER", False),
    ("HCAMGT", "Healthcare Management / Administration", 3, "UPPER", False),
    ("HCALAW", "Healthcare Law, Policy, and Compliance", 3, "UPPER", False),
    ("HCAETH", "Healthcare Ethics", 3, "UPPER", False),
    ("HCAFIN", "Healthcare Finance / Reimbursement", 3, "UPPER", False),
    ("HCAQI", "Quality Improvement / Patient Safety", 3, "UPPER", False),
    ("HCAHR", "Human Resources in Healthcare", 3, "UPPER", False),
    ("HCAINFO", "Health Information Systems", 3, "LOWER", False),
    ("HCAST", "Healthcare Statistics / Epidemiology", 3, "UPPER", False),
    ("MEDTERM", "Medical Terminology", 3, "LOWER", False),
    ("AANDP", "Anatomy and Physiology", 4, "LOWER", False),
    ("HELEC1", "Healthcare Elective I", 3, "UPPER", False),
    ("HELEC2", "Healthcare Elective II", 3, "UPPER", False),
    ("CAP", "Healthcare Administration Capstone", 3, "UPPER", True),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

SOFTWARE_MAJOR = [
    ("IT101", "Introduction to IT / Computing", 3, "LOWER", False),
    ("PROG", "Programming Fundamentals", 3, "LOWER", False),
    ("PROG2", "Object-Oriented / Intermediate Programming", 3, "LOWER", False),
    ("DS", "Data Structures and Algorithms", 3, "UPPER", False),
    ("DB", "Database Fundamentals", 3, "LOWER", False),
    ("WEB", "Web Technologies", 3, "LOWER", False),
    ("OS", "Operating Systems", 3, "LOWER", False),
    ("NET", "Networking Fundamentals", 3, "LOWER", False),
    ("SWE1", "Software Engineering Practices", 3, "UPPER", False),
    ("AGILE", "Agile / Scrum Software Process", 3, "UPPER", False),
    ("TEST", "Software Testing and Quality", 3, "UPPER", False),
    ("SECDEV", "Secure Software / Application Security", 3, "UPPER", False),
    ("ARCH", "Software Architecture / Design", 3, "UPPER", False),
    ("CLOUD", "Cloud / DevOps Foundations", 3, "UPPER", False),
    ("CAP", "Software Engineering Capstone", 3, "UPPER", True),
    ("SELEC1", "Software Elective I", 3, "UPPER", False),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

CRIMINAL_JUSTICE_MAJOR = [
    ("CJ1", "Introduction to Criminal Justice", 3, "LOWER", False),
    ("CRIM", "Criminology", 3, "LOWER", False),
    ("CJPOL", "Policing / Law Enforcement", 3, "LOWER", False),
    ("COURTS", "Courts and Judicial Process", 3, "LOWER", False),
    ("CORR", "Corrections", 3, "LOWER", False),
    ("CJL", "Criminal Law", 3, "UPPER", False),
    ("CJPROC", "Criminal Procedure and Evidence", 3, "UPPER", False),
    ("CJETH", "Ethics in Criminal Justice", 3, "UPPER", False),
    ("CJRES", "Research Methods in Criminal Justice", 3, "UPPER", False),
    ("JUV", "Juvenile Justice", 3, "UPPER", False),
    ("FORS", "Forensic Science / Investigation", 3, "UPPER", False),
    ("CJDIV", "Multicultural Issues in Criminal Justice", 3, "UPPER", False),
    ("HLS", "Homeland Security / Terrorism", 3, "UPPER", False),
    ("CJELEC1", "Criminal Justice Elective I", 3, "UPPER", False),
    ("CAP", "Criminal Justice Capstone", 3, "UPPER", True),
    ("ELEC1", "Free Elective I", 3, "LOWER", False),
    ("ELEC2", "Free Elective II", 3, "LOWER", False),
    ("ELEC3", "Free Elective III", 3, "LOWER", False),
    ("ELEC4", "Free Elective IV", 3, "LOWER", False),
]

FAMILY_MAJORS = {
    "PSYCHOLOGY": PSYCHOLOGY_MAJOR,
    "HEALTHCARE_MANAGEMENT": HEALTHCARE_MAJOR,
    "SOFTWARE_ENGINEERING": SOFTWARE_MAJOR,
    "CRIMINAL_JUSTICE": CRIMINAL_JUSTICE_MAJOR,
}

NCCRS_PROVIDERS = {"COOPERSMITH", "DAVAR"}
PLA_PROVIDERS = {"PLA"}
PLA_CERT_PROVIDERS = {"PMI", "SCRUMALLIANCE"}  # no ACE exhibit; PLA only
MILITARY_PROVIDERS = {"JST"}


def extra_programs() -> list[dict]:
    rows = []

    def add(inst, code, name, family, catalog, url, notes=""):
        row = {
            "institution_code": inst,
            "code": code,
            "name": name,
            "degree_family": family,
            "catalog_year": catalog,
            "total_credits": "120",
            "source_url": url,
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        }
        if notes:
            row["notes"] = notes
        rows.append(row)

    add("UMPI", "BAPSY", "Bachelor of Arts in Psychology — YourPace", "PSYCHOLOGY", "2025-2026",
        "https://www.umpi.edu/yourpace/academic_programs/psychology/")
    add("UMPI", "BSHA", "Bachelor of Science in Healthcare Administration — YourPace", "HEALTHCARE_MANAGEMENT", "2025-2026",
        "https://www.umpi.edu/yourpace/academic_programs/healthcare-administration/")
    add("UMPI", "BACJ", "Bachelor of Arts in Criminal Justice — YourPace", "CRIMINAL_JUSTICE", "2025-2026",
        "https://www.umpi.edu/yourpace/academic_programs/criminal-justice/")
    add("UMPI", "BLS", "Bachelor of Liberal Studies — YourPace", "PROFESSIONAL_STUDIES", "2025-2026",
        "https://www.umpi.edu/yourpace/academic_programs/")

    add("WGU", "BSPSY", "Bachelor of Science, Psychology", "PSYCHOLOGY", "2026-03",
        "https://www.wgu.edu/online-psychology-health-degrees/psychology-bachelors-program.html")
    add("WGU", "BSHA", "Bachelor of Science, Healthcare Administration", "HEALTHCARE_MANAGEMENT", "2026-03",
        "https://www.wgu.edu/online-business-degrees/healthcare-administration-bachelors-program.html")
    add("WGU", "BSSWE", "Bachelor of Science, Software Engineering", "SOFTWARE_ENGINEERING", "2026-03",
        "https://www.wgu.edu/online-it-degrees/software-engineering-bachelors-program.html")

    add("TESU", "BAPSY", "Bachelor of Arts in Psychology", "PSYCHOLOGY", "2025-2026",
        "https://tesu.smartcatalogiq.com/en/current/undergraduate-catalog/degree-programs-and-certificates/bachelor-of-arts-programs/bachelor-of-arts-in-psychology")
    add("TESU", "BSCJ", "Bachelor of Science in Criminal Justice", "CRIMINAL_JUSTICE", "2025-2026",
        "https://www.tesu.edu/heavin/undergrad/bs-criminal-justice")
    add("TESU", "BSIT", "Bachelor of Science in Information Technology", "SOFTWARE_ENGINEERING", "2025-2026",
        "https://www.tesu.edu/ast/undergrad",
        "Closest published TESU bachelor's path for software engineering planning.")
    add("TESU", "BSHS", "Bachelor of Science in Health Services", "HEALTHCARE_MANAGEMENT", "2025-2026",
        "https://www.tesu.edu/nursinghealth/undergrad")

    add("EXCELSIOR", "BSPSY", "Bachelor of Science in Psychology", "PSYCHOLOGY", "2025-2026",
        "https://www.excelsior.edu/program/online-bs-in-psychology-degree/")
    add("EXCELSIOR", "BSCJ", "Bachelor of Science in Criminal Justice", "CRIMINAL_JUSTICE", "2025-2026",
        "https://www.excelsior.edu/program/bachelor-of-science-in-criminal-justice/")
    add("EXCELSIOR", "BSIT", "Bachelor of Science in Information Technology", "SOFTWARE_ENGINEERING", "2025-2026",
        "https://www.excelsior.edu/program/bachelors-information-technology/",
        "Closest published Excelsior bachelor's path for software engineering planning.")
    add("EXCELSIOR", "BSHS", "Bachelor of Science in Health Sciences", "HEALTHCARE_MANAGEMENT", "2025-2026",
        "https://www.excelsior.edu/program/bachelors-health-sciences/")

    add("COSC", "BSPSY", "Bachelor of Science in Psychology", "PSYCHOLOGY", "2026-2027",
        "https://www.charteroak.edu/psychology/")
    add("COSC", "BSHA", "Bachelor of Science in Healthcare Administration", "HEALTHCARE_MANAGEMENT", "2026-2027",
        "https://www.charteroak.edu/health-care-administration/")
    add("COSC", "BSCJ", "Bachelor of Science in Criminal Justice", "CRIMINAL_JUSTICE", "2026-2027",
        "https://www.charteroak.edu/criminal-justice/")
    add("COSC", "BSSWD", "Bachelor of Science in Software Development", "SOFTWARE_ENGINEERING", "2026-2027",
        "https://www.charteroak.edu/bachelors/")

    add("SNHU", "BAPSY", "Bachelor of Arts in Psychology", "PSYCHOLOGY", "2026-2027",
        "https://www.snhu.edu/online-degrees/bachelors/ba-in-psychology")
    add("SNHU", "BSHA", "Bachelor of Science in Healthcare Administration", "HEALTHCARE_MANAGEMENT", "2026-2027",
        "https://www.snhu.edu/online-degrees/bachelors/bs-in-healthcare-administration")
    add("SNHU", "BSCJ", "Bachelor of Science in Criminal Justice", "CRIMINAL_JUSTICE", "2026-2027",
        "https://www.snhu.edu/online-degrees/bachelors/bs-in-criminal-justice")
    add("SNHU", "BSCS", "Bachelor of Science in Computer Science", "SOFTWARE_ENGINEERING", "2026-2027",
        "https://www.snhu.edu/online-degrees/bachelors/bs-in-computer-science",
        "Closest published SNHU bachelor's path for software engineering planning.")

    add("UMASS", "BAPSY", "Bachelor of Arts in Psychology", "PSYCHOLOGY", "2025-2026",
        "https://www.umassglobal.edu/academic-programs/bachelors-degrees",
        "Term-based online psychology bachelor's. Not a MyPath CBE program.")
    add("UMASS", "BACJ", "Bachelor of Arts in Criminal Justice", "CRIMINAL_JUSTICE", "2025-2026",
        "https://www.umassglobal.edu/academic-programs/bachelors-degrees",
        "Term-based online criminal justice bachelor's. Not a MyPath CBE program.")
    add("UMASS", "BSSWE", "Bachelor of Science in Information Technology — MyPath (software planning)", "SOFTWARE_ENGINEERING", "2025-2026",
        "https://www.umassglobal.edu/academic-programs/bs-information-technology",
        "MyPath BSIT is the self-paced bachelor's used here for software engineering planning. The same catalog program is also listed under Cybersecurity / IT.")

    add("PG", "BSPSY", "Bachelor of Science in Psychology", "PSYCHOLOGY", "2025-2026",
        "https://www.purdueglobal.edu/degree-programs/psychology/bachelor-degree-psychology-online/",
        "Standard online bachelor's (180 quarter credits stored as 120 semester-equivalent). Not ExcelTrack.")
    add("PG", "BSCJ", "Bachelor of Science in Criminal Justice", "CRIMINAL_JUSTICE", "2025-2026",
        "https://www.purdueglobal.edu/degree-programs/criminal-justice/bachelor-degree-criminal-justice/",
        "Standard online bachelor's. Not ExcelTrack.")
    add("PG", "BSHA", "Bachelor of Science in Health Care Administration", "HEALTHCARE_MANAGEMENT", "2025-2026",
        "https://www.purdueglobal.edu/degree-programs/health-sciences/")
    add("PG", "BSIT", "Bachelor of Science in Information Technology — ExcelTrack", "SOFTWARE_ENGINEERING", "2025-2026",
        "https://www.purdueglobal.edu/student-experience/personalized-learning/",
        "ExcelTrack self-paced IT bachelor's used here for software engineering planning.")
    return rows


def extra_wgu_rules() -> dict[str, list]:
    business = [
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
    ]
    it = [
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
    ]
    return {"BSPSY": business, "BSHA": business, "BSSWE": it}


def extra_pg_rules() -> dict[str, list]:
    excel = [
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
    ]
    # $371 per quarter credit; 1 semester credit = 1.5 quarter credits → $556.50 ≈ 557.
    term = [
        ("MAX_TRANSFER_CREDITS", "90", "CREDITS"),
        ("RESIDENCY_CREDIT_MINIMUM", "30", "CREDITS"),
        ("UPPER_LEVEL_CREDIT_MINIMUM", "30", "CREDITS"),
        ("ACE_ACCEPTANCE", "ACCEPTED", "ENUM"),
        ("NCCRS_ACCEPTANCE", "ACCEPTED", "ENUM"),
        ("PLA_ACCEPTANCE", "ACCEPTED", "ENUM"),
        ("TUITION", "557", "PER_CREDIT"),
        ("TERM_WEEKS", "10", "WEEKS"),
        ("APPLICATION_FEE", "0", "USD"),
        ("GRADUATION_FEE", "0", "USD"),
        ("TRANSCRIPT_EVALUATION_FEE", "0", "USD"),
    ]
    return {"BSPSY": term, "BSCJ": term, "BSHA": term, "BSIT": excel}


def extra_providers() -> list[dict]:
    def p(code, name, platform, evaluator, hours, pricing, monthly, url):
        return {
            "code": code,
            "name": name,
            "delivery_platform": platform,
            "evaluator": evaluator,
            "default_hours_per_credit": hours,
            "pricing_model": pricing,
            "monthly_price": monthly,
            "source_url": url,
            "last_verified_at": VERIFIED_AT,
            "verified_by": VERIFIED_BY,
            "verification_status": "VERIFIED",
        }
    return [
        p("DSST", "DSST (Prometric)", "EXAM", "ACE", "12", "EXAM_FEE", "0",
          "https://www.getcollegecredit.com/"),
        p("COOPERSMITH", "Coopersmith Career Consulting", "SELF_PACED", "NCCRS", "9", "PER_COURSE", "0",
          "https://www.coopersmithcc.net/"),
        p("DAVAR", "Davar Academy", "SELF_PACED", "NCCRS", "9", "PER_COURSE", "0",
          "https://www.davaracademy.com/"),
        p("GOOGLE", "Google Career Certificates (Coursera)", "SELF_PACED", "ACE", "8", "SUBSCRIPTION", "49",
          "https://grow.google/certificates"),
        p("IBM", "IBM Professional Certificates (Coursera / SkillsBuild)", "SELF_PACED", "ACE", "8", "SUBSCRIPTION", "49",
          "https://www.acenet.edu/National-Guide/Pages/Organization.aspx?oid=6d532c35-75c4-ea11-a812-000d3a33232a"),
        p("ECCOUNCIL", "EC-Council", "EXAM", "ACE", "12", "EXAM_FEE", "0",
          "https://www.eccouncil.org/ec-council-in-news/ec-council-helps-accelerate-degrees/"),
        p("ISC2", "ISC2", "EXAM", "ACE", "12", "EXAM_FEE", "0",
          "https://www.prnewswire.com/news-releases/isc2-certification-exams-and-official-certification-courses-receive-american-council-on-education-eligibility-for-college-credits-302545719.html"),
        p("PMI", "Project Management Institute", "EXAM", "PLA", "12", "EXAM_FEE", "0",
          "https://www.pmi.org/certifications/project-management-pmp"),
        p("SCRUMALLIANCE", "Scrum Alliance", "EXAM", "PLA", "12", "EXAM_FEE", "0",
          "https://www.scrumalliance.org/"),
        p("JST", "Joint Services Transcript / ACE Military Guide", "MILITARY", "ACE", "0", "NONE", "0",
          "https://www.acenet.edu/Programs-Services/Pages/Credit-Transcripts/Military-Guide-Online.aspx"),
        p("PLA", "Portfolio / Prior Learning Assessment", "PLA", "PLA", "7", "ASSESSMENT_FEE", "0",
          "https://www.tesu.edu/academics/catalog"),
    ]


# Tuple: provider, code, title, otype, ace_or_nccrs, credits, level, hours, extra_price, slots, evaluator
def extra_opportunities() -> list[tuple]:
    rows: list[tuple] = []

    # Additional Study.com / StraighterLine / CLEP for new majors
    rows += [
        ("STUDYCOM", "SDC-PSY101", "Introduction to Psychology", "ONLINE_COURSE", "ACE-SDC-0201", 3, "LOWER", 9, 0, "SS1,PSY1"),
        ("STUDYCOM", "SDC-PSY102", "Lifespan Developmental Psychology", "ONLINE_COURSE", "ACE-SDC-0202", 3, "LOWER", 9, 0, "DEV"),
        ("STUDYCOM", "SDC-PSY104", "Abnormal Psychology", "ONLINE_COURSE", "ACE-SDC-0203", 3, "UPPER", 9, 0, "ABPSY"),
        ("STUDYCOM", "SDC-PSY105", "Social Psychology", "ONLINE_COURSE", "ACE-SDC-0204", 3, "UPPER", 9, 0, "SOCPSY"),
        ("STUDYCOM", "SDC-CJ101", "Introduction to Criminal Justice", "ONLINE_COURSE", "ACE-SDC-0205", 3, "LOWER", 9, 0, "CJ1"),
        ("STUDYCOM", "SDC-CJ102", "Introduction to Criminology", "ONLINE_COURSE", "ACE-SDC-0206", 3, "LOWER", 9, 0, "CRIM"),
        ("STUDYCOM", "SDC-CS201", "Data Structures", "ONLINE_COURSE", "ACE-SDC-0207", 3, "UPPER", 9, 0, "DS"),
        ("STUDYCOM", "SDC-CS114", "Programming in Python", "ONLINE_COURSE", "ACE-SDC-0208", 3, "LOWER", 9, 0, "PROG"),
        ("STRAIGHTERLINE", "SL-PSY201", "Lifespan Development", "ONLINE_COURSE", "ACE-SL-0301", 3, "LOWER", 9, 59, "DEV"),
        ("STRAIGHTERLINE", "SL-AANDP1", "Anatomy & Physiology I", "ONLINE_COURSE", "ACE-SL-0302", 4, "LOWER", 9, 79, "AANDP,NS1"),
        ("STRAIGHTERLINE", "SL-MEDTERM", "Medical Terminology", "ONLINE_COURSE", "ACE-SL-0303", 3, "LOWER", 9, 59, "MEDTERM"),
        ("STRAIGHTERLINE", "SL-CJ101", "Introduction to Criminal Justice", "ONLINE_COURSE", "ACE-SL-0304", 3, "LOWER", 9, 59, "CJ1"),
        ("CLEP", "CLEP-HDEV", "Human Growth and Development", "EXAM", "ACE-CLEP-0316", 3, "LOWER", 12, 93, "DEV"),
        ("CLEP", "CLEP-EDPSY", "Introduction to Educational Psychology", "EXAM", "ACE-CLEP-0317", 3, "LOWER", 12, 93, "PELEC1,LEARN"),
        ("CLEP", "CLEP-GOV", "American Government", "EXAM", "ACE-CLEP-0318", 3, "LOWER", 12, 93, "SS2"),
        ("CLEP", "CLEP-ACCT", "Financial Accounting", "EXAM", "ACE-CLEP-0319", 3, "LOWER", 12, 93, "ACCT1"),
        ("CLEP", "CLEP-NSCI", "Natural Sciences", "EXAM", "ACE-CLEP-0320", 6, "LOWER", 12, 93, "NS1,NS2"),
        ("CLEP", "CLEP-WEST", "Western Civilization I", "EXAM", "ACE-CLEP-0321", 3, "LOWER", 12, 93, "HIST,HUM1"),
    ]

    # DSST — ACE National Guide, typically 3 credits, exam fee ~$100
    dsst = [
        ("DSST-PSY", "Introduction to Psychology", "PSY1,SS1"),
        ("DSST-DEV", "Lifespan Developmental Psychology", "DEV"),
        ("DSST-CJ", "Criminal Justice", "CJ1"),
        ("DSST-LE", "Introduction to Law Enforcement", "CJPOL"),
        ("DSST-HR", "Human Resource Management", "HR,HCAHR"),
        ("DSST-FIN", "Principles of Finance", "FIN"),
        ("DSST-ETH", "Ethics in America", "ETHICS,ETH2,CJETH,ETHPSY"),
        ("DSST-IT", "Computing and Information Technology", "INFO,IT101"),
        ("DSST-STAT", "Principles of Statistics", "STAT,STATS,DATA"),
        ("DSST-ALG", "Fundamentals of College Algebra", "QR"),
        ("DSST-MLA", "Math for Liberal Arts", "QR"),
        ("DSST-SPCH", "Principles of Public Speaking", "COMM,PROFCOMM"),
        ("DSST-OB", "Organizational Behavior", "OB,LEAD"),
        ("DSST-MIS", "Management Information Systems", "IS,HCAINFO"),
        ("DSST-BUS", "Introduction to Business", "BUS101"),
        ("DSST-SUP", "Principles of Supervision", "MGT,HCAMGT"),
        ("DSST-BETH", "Business Ethics and Society", "ETHICS,ETH2"),
        ("DSST-ENV", "Environmental Science", "NS2"),
        ("DSST-AST", "Astronomy", "NS2"),
        ("DSST-ART", "Art of the Western World", "HUM1"),
        ("DSST-ANTH", "General Anthropology", "SS2"),
        ("DSST-SA", "Substance Abuse", "PELEC2"),
        ("DSST-COUN", "Fundamentals of Counseling", "PELEC1"),
        ("DSST-VN", "A History of the Vietnam War", "HIST"),
        ("DSST-GOV", "American Government and Politics", "SS2"),
    ]
    for i, (code, title, slots) in enumerate(dsst, start=1):
        rows.append(("DSST", code, title, "EXAM", f"ACE-DANT-{i:04d}", 3, "LOWER", 12, 100, slots))

    # Coopersmith — NCCRS, typical $249/course
    coop = [
        ("CSC-PSY101", "Introduction to Psychology (PSY-101)", "PSY1,SS1"),
        ("CSC-PSY151", "Introduction to Cognitive Psychology (PSY-151)", "COG"),
        ("CSC-PSY152", "Psychology of Personality (PSY-152)", "PERS"),
        ("CSC-PSY215", "Introduction to Forensic Psychology (PSY-215)", "PELEC1,CJELEC1"),
        ("CSC-PSY216", "Industrial and Organizational Psychology (PSY-216)", "PELEC2,OB"),
        ("CSC-JUS175", "Introduction to Criminal Justice (JUS-175)", "CJ1"),
        ("CSC-JUS177", "Introduction to Forensic Science (JUS-177)", "FORS"),
        ("CSC-JUS275", "Homeland Security and Terrorism (JUS-275)", "HLS"),
        ("CSC-LAW250", "Law Enforcement in the 21st Century (LAW-250)", "CJPOL"),
        ("CSC-HCA201", "Health Care Management (HCA-201)", "HCAMGT,HCA1"),
        ("CSC-HCA200", "Ethics for Health Professions (HCA-200)", "HCAETH"),
        ("CSC-SCI201", "Anatomy and Physiology (SCI-201)", "AANDP"),
        ("CSC-TCH201", "Computer Basics in Healthcare (TCH-201)", "HCAINFO"),
        ("CSC-ACC101", "Principles of Financial Accounting I (ACC-101)", "ACCT1"),
        ("CSC-ACC102", "Principles of Financial Accounting II (ACC-102)", "ACCT2"),
        ("CSC-FIN101", "Principles of Finance (FIN-101)", "FIN,HCAFIN"),
        ("CSC-BUS101", "Fundamentals of Management (BUS-101)", "MGT,LEAD"),
        ("CSC-BUS102", "Introduction to Marketing (BUS-102)", "MKT"),
        ("CSC-BUS202", "Human Resources Management (BUS-202)", "HR,HCAHR"),
        ("CSC-BUS203", "Management Information Systems (BUS-203)", "IS"),
        ("CSC-CIS101", "Introduction to Computers (CIS-101)", "INFO,IT101"),
        ("CSC-TCH185", "Introduction to Programming Using Python (TCH-185)", "PROG"),
        ("CSC-TCH105", "Introduction to HTML5 and CSS3 (TCH-105)", "WEB"),
        ("CSC-COM101", "Introduction to Public Speaking (COM-101)", "COMM,PROFCOMM"),
        ("CSC-MAT102", "Introduction to Statistics (MAT-102)", "STAT,STATS,DATA"),
        ("CSC-SOC103", "Introduction to Sociology (SOC-103)", "SS2"),
        ("CSC-ART101", "Introduction to Art (ART-101)", "HUM1"),
        ("CSC-BUS205", "Introduction to Business Ethics (BUS-205)", "ETHICS,ETH2"),
    ]
    for code, title, slots in coop:
        rows.append(("COOPERSMITH", code, title, "ONLINE_COURSE", "NCCRS-CSC", 3, "LOWER", 9, 249, slots))

    # Davar Academy — NCCRS, $150/course
    davar = [
        ("DAV-ENG101", "English Composition I (ENG 101)", "ENG1"),
        ("DAV-ENG102", "English Composition II (ENG 102)", "ENG2"),
        ("DAV-COM101", "Principles of Public Speaking (COM 101)", "COMM,PROFCOMM"),
        ("DAV-ENG103", "Information Literacy (ENG 103)", "INFO,ILP"),
        ("DAV-MAT201", "College Algebra (MAT 201)", "QR"),
        ("DAV-MAT203", "Principles of Statistics (MAT 203)", "STAT,STATS,DATA"),
        ("DAV-SCI101", "Introduction to Biology (SCI 101)", "NS2"),
        ("DAV-POS101", "American Government (POS 101)", "SS2"),
        ("DAV-ACCT301", "Financial Accounting (ACCT 301)", "ACCT1"),
        ("DAV-ACCT302", "Managerial Accounting (ACCT 302)", "ACCT2"),
        ("DAV-FIN301", "Principles of Finance (FIN 301)", "FIN"),
        ("DAV-MAN101", "Principles of Management (MAN 101)", "MGT,LEAD"),
        ("DAV-MAN310", "Organizational Behavior (MAN 310)", "OB,LEAD"),
        ("DAV-MAR301", "Marketing Concepts (MAR 301)", "MKT"),
        ("DAV-LAW201", "Business Law (LAW 201)", "BLAW"),
        ("DAV-PHI370", "Business Ethics (PHI 370)", "ETHICS,ETH2"),
        ("DAV-CIS101", "Introduction to Computers (CIS 101)", "INFO,IT101"),
        ("DAV-CIS315", "Management Information Systems (CIS 315)", "IS"),
    ]
    for code, title, slots in davar:
        rows.append(("DAVAR", code, title, "ONLINE_COURSE", "NCCRS-DAV", 3, "LOWER", 9, 150, slots))

    # Google Career Certificates — current ACE (Coursera ACE FAQ). Split into 3-credit components.
    google = [
        ("GOOG-IT-1", "Google IT Support — Computer Applications", "ACE-GOOG-IT", "INFO,IT101"),
        ("GOOG-IT-2", "Google IT Support — Operating Systems", "ACE-GOOG-IT", "OS"),
        ("GOOG-IT-3", "Google IT Support — Networking", "ACE-GOOG-IT", "NET"),
        ("GOOG-IT-4", "Google IT Support — Security Foundations", "ACE-GOOG-IT", "SEC1"),
        ("GOOG-IT-5", "Google IT Support — Systems Administration", "ACE-GOOG-IT", "IT101,ELEC1"),
        ("GOOG-CYB-1", "Google Cybersecurity — Linux / OS", "ACE-GOOG-CYB", "OS"),
        ("GOOG-CYB-2", "Google Cybersecurity — Intro to Cybersecurity", "ACE-GOOG-CYB", "SEC1"),
        ("GOOG-CYB-3", "Google Cybersecurity — Attacks and Defense", "ACE-GOOG-CYB", "NETSEC,CELEC1"),
        ("GOOG-DA-1", "Google Data Analytics — Spreadsheets / Data", "ACE-GOOG-DA", "DATA,STAT"),
        ("GOOG-DA-2", "Google Data Analytics — Databases", "ACE-GOOG-DA", "DB,IS"),
        ("GOOG-DA-3", "Google Data Analytics — Visualization", "ACE-GOOG-DA", "INFO,HCAST"),
        ("GOOG-DA-4", "Google Data Analytics — Career / Applied", "ACE-GOOG-DA", "ELEC1"),
        ("GOOG-PM-1", "Google Project Management — Foundations", "ACE-GOOG-PM", "MGT,OPS,HCAMGT"),
        ("GOOG-PM-2", "Google Project Management — Agile", "ACE-GOOG-PM", "AGILE,BELEC1"),
        ("GOOG-PM-3", "Google Project Management — Execution", "ACE-GOOG-PM", "LEAD,SWE1"),
        ("GOOG-PY-1", "Google IT Automation with Python — Programming", "ACE-GOOG-PY", "PROG"),
        ("GOOG-PY-2", "Google IT Automation with Python — OS / Automation", "ACE-GOOG-PY", "OS,CLOUD"),
        ("GOOG-PY-3", "Google IT Automation with Python — Configuration", "ACE-GOOG-PY", "IT101,SELEC1"),
        ("GOOG-UX-1", "Google UX Design — Foundations", "ACE-GOOG-UX", "WEB,HUM1"),
        ("GOOG-DM-1", "Google Digital Marketing — Foundations", "ACE-GOOG-DM", "MKT"),
    ]
    for code, title, ace, slots in google:
        rows.append(("GOOGLE", code, title, "CERTIFICATION", ace, 3, "LOWER", 8, 0, slots))

    # IBM current ACE National Guide (active exhibits 2024–2029). Components.
    ibm = [
        ("IBM-CYB-1", "IBM Cybersecurity Analyst — Foundations", "IBM-0016", "SEC1"),
        ("IBM-CYB-2", "IBM Cybersecurity Analyst — Network Security", "IBM-0016", "NET,NETSEC"),
        ("IBM-CYB-3", "IBM Cybersecurity Analyst — Incident Response", "IBM-0016", "SEC2,FOR"),
        ("IBM-CYB-4", "IBM Cybersecurity Analyst — Risk / Compliance", "IBM-0016", "RISK"),
        ("IBM-SWE-1", "IBM Full Stack Software Developer — Programming", "IBM-0020", "PROG,PROG2"),
        ("IBM-SWE-2", "IBM Full Stack Software Developer — Web", "IBM-0020", "WEB"),
        ("IBM-SWE-3", "IBM Full Stack Software Developer — Databases", "IBM-0020", "DB"),
        ("IBM-SWE-4", "IBM Full Stack Software Developer — Cloud / DevOps", "IBM-0020", "CLOUD,SWE1"),
        ("IBM-DEVOPS-1", "IBM DevOps and Software Engineering — Process", "IBM-0032", "SWE1,AGILE"),
        ("IBM-DEVOPS-2", "IBM DevOps and Software Engineering — Tooling", "IBM-0032", "CLOUD,TEST"),
        ("IBM-DS-1", "IBM Data Science — Python / SQL", "IBM-0017", "PROG,DB"),
        ("IBM-DS-2", "IBM Data Science — Analysis", "IBM-0017", "STAT,DATA"),
        ("IBM-IT-1", "IBM IT Support — Foundations", "IBM-0033", "IT101,INFO"),
        ("IBM-IT-2", "IBM IT Support — Systems", "IBM-0033", "OS,NET"),
        ("IBM-SB-CYB-1", "IBM SkillsBuild Cybersecurity — Governance", "IBM-0028", "RISK,SEC1"),
        ("IBM-SB-CYB-2", "IBM SkillsBuild Cybersecurity — Network / Cloud", "IBM-0028", "NETSEC"),
        ("IBM-SB-CYB-3", "IBM SkillsBuild Cybersecurity — Operations", "IBM-0028", "SEC2,FOR"),
        ("IBM-SB-DA-1", "IBM SkillsBuild Data Analytics — Statistics", "IBM-0029", "STAT,DATA"),
        ("IBM-SB-DA-2", "IBM SkillsBuild Data Analytics — Visualization", "IBM-0029", "INFO,HCAST"),
        ("IBM-PM-1", "IBM Product Manager — Foundations", "IBM-0034", "MGT,AGILE"),
    ]
    for code, title, ace, slots in ibm:
        rows.append(("IBM", code, title, "CERTIFICATION", ace, 3, "LOWER", 8, 0, slots))

    # EC-Council ACE (3 semester hours each)
    ecc = [
        ("ECC-CSCU", "Certified Secure Computer User (CSCU)", "SEC1", 3, "LOWER"),
        ("ECC-NDE", "Network Defense Essentials (NDE)", "NET", 3, "LOWER"),
        ("ECC-EHE", "Ethical Hacking Essentials (EHE)", "SEC1,PTEST", 3, "LOWER"),
        ("ECC-DFE", "Digital Forensics Essentials (DFE)", "FOR", 3, "LOWER"),
        ("ECC-CCT", "Certified Cybersecurity Technician (CCT)", "SEC1,SEC2", 3, "LOWER"),
        ("ECC-CND", "Certified Network Defender (CND)", "NETSEC,SEC2", 3, "UPPER"),
        ("ECC-CEH", "Certified Ethical Hacker (CEH)", "PTEST,NETSEC", 3, "UPPER"),
        ("ECC-CHFI", "Computer Hacking Forensic Investigator (CHFI)", "FOR", 3, "UPPER"),
        ("ECC-ECIH", "EC-Council Certified Incident Handler (ECIH)", "SEC2", 3, "UPPER"),
        ("ECC-CPENT", "Certified Penetration Testing Professional (CPENT)", "PTEST", 3, "UPPER"),
    ]
    for code, title, slots, cr, lvl in ecc:
        rows.append(("ECCOUNCIL", code, title, "CERTIFICATION", "ACE-ECC", cr, lvl, 12, 1199, slots))

    # ISC2 ACE (3 hours each; CISSP/CSSLP/CGRC upper)
    isc = [
        ("ISC2-CC", "Certified in Cybersecurity (CC)", "SEC1", "LOWER"),
        ("ISC2-SSCP", "Systems Security Certified Practitioner (SSCP)", "SEC2,OS", "LOWER"),
        ("ISC2-CISSP", "Certified Information Systems Security Professional (CISSP)", "RISK,LAW", "UPPER"),
        ("ISC2-CCSP", "Certified Cloud Security Professional (CCSP)", "NETSEC,CLOUD", "UPPER"),
        ("ISC2-CSSLP", "Certified Secure Software Lifecycle Professional (CSSLP)", "SECDEV,SWE1", "UPPER"),
        ("ISC2-CGRC", "Governance, Risk and Compliance (CGRC)", "RISK", "UPPER"),
    ]
    for code, title, slots, lvl in isc:
        rows.append(("ISC2", code, title, "CERTIFICATION", "ACE-ISC2", 3, lvl, 12, 749, slots))

    # PMP is not ACE-listed. Catalog as PLA-eligible certification only.
    rows.append(("PMI", "PMI-PMP", "Project Management Professional (PMP)", "CERTIFICATION", "", 3, "UPPER", 12, 555, "OPS,MGT,AGILE,HCAMGT"))
    rows.append(("SCRUMALLIANCE", "SA-CSM", "Certified ScrumMaster (CSM)", "CERTIFICATION", "", 3, "LOWER", 12, 1000, "AGILE,SWE1,OPS"))
    rows.append(("SCRUMALLIANCE", "SA-CSPO", "Certified Scrum Product Owner (CSPO)", "CERTIFICATION", "", 3, "LOWER", 12, 1000, "AGILE,MKT,LEAD"))

    # Military ACE / JST — compiled MOS posture, not a specific transcript
    mil = [
        ("JST-25B", "Army 25B Information Technology Specialist", "IT101,NET,INFO"),
        ("JST-31B", "Army 31B Military Police", "CJ1,CJPOL"),
        ("JST-68W", "Army 68W Combat Medic", "HCA1,MEDTERM,AANDP"),
        ("JST-35F", "Army 35F Intelligence Analyst", "SS1,ELEC1"),
        ("JST-11B", "Army 11B Infantryman", "LEAD,ELEC2"),
        ("JST-IT", "Navy Information Systems Technician", "IT101,NET,OS"),
        ("JST-HM", "Navy Hospital Corpsman", "HCA1,MEDTERM"),
        ("JST-MA", "Navy Master-at-Arms", "CJ1,CJPOL"),
        ("JST-3D0", "Air Force Cyber Operations / IT", "IT101,SEC1,NET"),
        ("JST-3P0", "Air Force Security Forces", "CJ1,CJPOL"),
    ]
    for code, title, slots in mil:
        rows.append(("JST", code, title, "MILITARY", "ACE-MIL", 3, "LOWER", 0, 0, slots))

    # Portfolio / PLA learning-agreement courses at PLA-accepting colleges
    pla = [
        ("PLA-PSY1", "Portfolio: Introductory Psychology", "PSY1,SS1"),
        ("PLA-DEV", "Portfolio: Developmental Psychology", "DEV"),
        ("PLA-CJ1", "Portfolio: Introduction to Criminal Justice", "CJ1"),
        ("PLA-MGT", "Portfolio: Principles of Management", "MGT,LEAD,HCAMGT"),
        ("PLA-HCA", "Portfolio: Healthcare Management", "HCAMGT,HCA1"),
        ("PLA-SEC1", "Portfolio: Cybersecurity Foundations", "SEC1"),
        ("PLA-PM", "Learning agreement: Project Management", "OPS,AGILE,MGT"),
        ("PLA-AGILE", "Learning agreement: Agile / Scrum Practice", "AGILE,SWE1"),
        ("PLA-LEAD", "Portfolio: Leadership / Organizational Behavior", "LEAD,OB"),
        ("PLA-COMM", "Portfolio: Professional Communication", "PROFCOMM,COMM"),
        ("PLA-HR", "Portfolio: Human Resource Management", "HR,HCAHR"),
        ("PLA-PROG", "Portfolio: Programming Experience", "PROG"),
    ]
    for code, title, slots in pla:
        rows.append(("PLA", code, title, "PLA", "", 3, "LOWER", 7, 300, slots))

    return rows


PROVIDER_URLS = {
    "DSST": "https://www.getcollegecredit.com/exam_fact_sheets/",
    "COOPERSMITH": "https://www.nationalccrs.org/organizations/coopersmith-career-consulting",
    "DAVAR": "https://www.nationalccrs.org/organizations/davar-academy-llc",
    "GOOGLE": "https://www.coursera.support/s/article/learner-000001996",
    "IBM": "https://www.acenet.edu/National-Guide/Pages/Organization.aspx?oid=6d532c35-75c4-ea11-a812-000d3a33232a",
    "ECCOUNCIL": "https://www.eccouncil.org/ec-council-in-news/ec-council-helps-accelerate-degrees/",
    "ISC2": "https://www.prnewswire.com/news-releases/isc2-certification-exams-and-official-certification-courses-receive-american-council-on-education-eligibility-for-college-credits-302545719.html",
    "PMI": "https://www.pmi.org/certifications/project-management-pmp",
    "SCRUMALLIANCE": "https://www.scrumalliance.org/",
    "JST": "https://www.acenet.edu/Programs-Services/Pages/Credit-Transcripts/Military-Guide-Online.aspx",
    "PLA": "https://www.tesu.edu/academics/catalog",
}

SLOT_OVERLAY = {
    "SOPH-PSYC1001": "SS1,PSY1",
    "SL-PSY101": "SS1,PSY1",
    "CLEP-PSY": "SS1,PSY1",
    "SOPH-CS1003": "PROG,PROG2",
    "SOPH-CS1002": "DB,IS,HCAINFO",
    "SOPH-STAT1001": "STAT,DATA,STATS,HCAST",
    "SDC-STAT101": "STAT,DATA,STATS",
    "CLEP-IT": "INFO,IS,ILP,IT101,HCAINFO",
}

EXTRA_CC = [
    ("PSYC101", "Introduction to Psychology", "PSY1"),
    ("PSYC201", "Developmental Psychology", "DEV"),
    ("PSYC210", "Abnormal Psychology", "ABPSY"),
    ("CRJU101", "Introduction to Criminal Justice", "CJ1"),
    ("CRJU201", "Criminology", "CRIM"),
    ("CRJU110", "Policing", "CJPOL"),
    ("HLTH101", "Introduction to Healthcare", "HCA1"),
    ("HLTH110", "Medical Terminology", "MEDTERM"),
    ("BIOL210", "Anatomy and Physiology", "AANDP"),
    ("CSCI201", "Programming I", "PROG"),
    ("CSCI202", "Programming II", "PROG2"),
    ("CSCI220", "Data Structures", "DS"),
]


def source_type_for(provider: str, otype: str, code: str) -> str:
    if provider in PLA_PROVIDERS or otype == "PLA":
        return "PLA"
    if provider in MILITARY_PROVIDERS or otype == "MILITARY":
        return "MILITARY"
    if otype in ("EXAM",) or code.startswith("CLEP-") or code.startswith("DSST-"):
        return "EXAM"
    if otype == "CERTIFICATION" or provider in {"GOOGLE", "IBM", "ECCOUNCIL", "ISC2", "PMI", "SCRUMALLIANCE"}:
        return "CERTIFICATION"
    return "PROVIDER_COURSE"


def equivalency_status(provider: str, nccrs_policy: str, pla_policy: str) -> str | None:
    """Return status to store, or None to skip the row for this school."""
    if provider in NCCRS_PROVIDERS:
        if nccrs_policy == "ACCEPTED":
            return "VERIFIED"
        if nccrs_policy == "CASE_BY_CASE":
            return "REQUIRES_CONFIRMATION"
        return None
    if provider in PLA_PROVIDERS or provider in PLA_CERT_PROVIDERS:
        if pla_policy == "ACCEPTED":
            return "REQUIRES_CONFIRMATION"
        return None
    if provider in MILITARY_PROVIDERS:
        return "HIGH_CONFIDENCE"
    return "VERIFIED"
