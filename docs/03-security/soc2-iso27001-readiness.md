# SOC 2 and ISO/IEC 27001 readiness baseline

Status: implementation baseline, not a certification or audit opinion.
Owner: Degree Agency security / system owner
Review cadence: quarterly and after material system changes

## Scope

Initial scope is the Degree Agency application and supporting SDLC/cloud environment represented by this repository: React web client, Java/Spring API, PostgreSQL data store, GitHub Actions CI/CD, container build assets, Azure deployment/IaC, and the operational processes required to run them.

Production scope must be finalized before an audit and must identify all in-scope vendors, personnel, endpoints, cloud subscriptions, DNS/CDN/WAF services, monitoring services, payment providers, support systems, and data stores.

## Target frameworks

- SOC 2: Security is the baseline Trust Services Category. Availability and Confidentiality should be included when customer commitments and production architecture support them. Processing Integrity and Privacy require a separate scope decision.
- ISO/IEC 27001:2022: establish and operate an ISMS, including organizational context, leadership, risk assessment/treatment, objectives, operational controls, performance evaluation, improvement, and an Annex A Statement of Applicability.

## Current engineering evidence

| Area | Existing evidence | Readiness action |
| --- | --- | --- |
| Identity | Argon2id password hashing; role-based admin routes; short-lived JWTs; rotating hashed refresh tokens | Add MFA for privileged users; eliminate browser-persistent bearer/refresh tokens; document joiner/mover/leaver process |
| Change management | Git history, pull requests, CI tests | Protect `main`; require PR review and passing checks; document emergency changes |
| Secure SDLC | Unit tests, SBOM generation, secret scanning | CI now fails closed for secret scan/SBOM; add SAST/container/IaC scanning and remediation SLAs |
| Secrets | Environment variables, Azure Key Vault target, Azure OIDC | Require production secrets from managed secret store; rotate on schedule/incident; prohibit defaults in production |
| Logging | Application audit-event model; Azure Log Analytics target | Define security event coverage, immutable/central retention, alerting, access review, and retention period |
| Data protection | Local transcript parsing; PostgreSQL; privacy page | Complete data inventory/flow, classification, retention/deletion, encryption-at-rest/in-transit evidence, and backup protection |
| Availability | Health endpoints; DR/backup docs exist | Implement monitored backups, restore tests, RTO/RPO, capacity monitoring, incident communications |
| Vulnerability management | Dependency files, CI | Dependabot enabled; establish vulnerability triage/remediation SLA and recurring external testing |
| Cloud/IaC | Terraform, Azure OIDC | Separate prod/non-prod, least privilege, private networking as appropriate, diagnostic settings, policy enforcement, production approval gates |

## P0 blockers before claiming audit readiness

1. Make the production source repository private unless there is an intentional open-source governance decision.
2. Enforce branch/ruleset protection for `main`: pull requests, at least one independent approval, passing CI/security checks, no force pushes, no direct production changes.
3. Require MFA for GitHub, cloud, email/admin, and every privileged production account; prefer phishing-resistant MFA where supported.
4. Remove browser-persistent authentication tokens. Prefer Secure + HttpOnly + SameSite cookies for refresh/session material and keep short-lived access credentials out of persistent browser storage.
5. Remove/disable all production default credentials and fail startup/deployment when production secrets are absent or weak.
6. Centralize production logs and security audit events, protect them from alteration, define retention, and alert on authentication/admin/security events.
7. Implement encrypted, monitored database backups and complete at least one documented restoration test.
8. Complete the asset/vendor/data inventory and assign owners and classifications.
9. Establish incident response, vulnerability remediation, access review, change management, backup/restore, vendor risk, and business continuity evidence cycles.
10. Perform a formal risk assessment and risk treatment plan; for ISO 27001 create and approve the Statement of Applicability.

## Evidence register

Maintain evidence outside source code when it contains confidential operational information. Minimum recurring evidence should include:

- quarterly access reviews and privileged-access approvals
- security/MFA configuration exports or screenshots
- branch/ruleset configuration and PR samples
- CI/security scan results and remediation tickets
- production deployment approvals and release records
- asset, data, and vendor inventories
- risk register, treatment decisions, and accepted risks
- security awareness completion
- vulnerability/patch reports and penetration-test remediation
- incident tickets, postmortems, and tabletop exercises
- backup success reports and restore-test results
- monitoring/alert samples and log-retention configuration
- vendor due diligence and security agreements
- ISO internal-audit results, management-review minutes, corrective actions, ISMS objectives, and Statement of Applicability

## Recommended readiness sequence

### Phase 1 — engineering guardrails

Harden identity, CI/CD, secrets, repository governance, production configuration, logging, backup, and environment separation. No audit claim is made in this phase.

### Phase 2 — management system and evidence

Define ISMS/SOC scope, owners, policies, risk register, vendor register, data inventory, access review, incident/BCP exercises, vulnerability SLAs, evidence retention, security training, internal audit, and management review.

### Phase 3 — observation and audit

For SOC 2 Type I, validate design at a point in time. For SOC 2 Type II, operate controls consistently through the auditor-selected observation period. For ISO/IEC 27001 certification, complete the ISMS implementation, internal audit, management review, corrective actions, Stage 1 readiness review, and Stage 2 certification audit with an accredited certification body.

## Claim language

Until an independent examination/certification is completed, use language such as `designed toward SOC 2 and ISO/IEC 27001 readiness` or `controls mapped to SOC 2 and ISO/IEC 27001`. Do not state that Degree Agency is SOC 2 compliant, SOC 2 certified, ISO 27001 compliant, or ISO 27001 certified solely because these controls or documents exist.
