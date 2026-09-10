# Compliance control map

This is an engineering control map and readiness aid, not a certification or audit claim.

## Framework coverage

The Degree Agency security program maps engineering and operational controls to:

- SOC 2 Trust Services Criteria, beginning with the Security category and adding Availability/Confidentiality when included in customer commitments and audit scope.
- ISO/IEC 27001:2022 ISMS requirements and the organization's risk-based Annex A Statement of Applicability.
- NIST Cybersecurity Framework and NIST Privacy Framework.
- NIST SP 800-53 control families where useful for detailed implementation guidance.
- OWASP ASVS, OWASP Top 10, and OWASP API Security Top 10 for application-security verification.
- Privacy-by-design practices reflected in IAPP CIPT and ISACA CDPSE bodies of knowledge.

## Repository evidence anchors

| Control theme | Repository evidence |
| --- | --- |
| Access control | `apps/api/.../SecurityConfig.java`, `identity/`, admin role enforcement |
| Credential protection | Argon2id password hashing, short-lived signed JWTs, hashed rotating refresh tokens |
| Auditability | `apps/api/.../audit/`, Git history, pull requests, CI results |
| Secure change | `.github/workflows/ci.yml`, tests, dependency review, SBOM generation |
| Supply-chain security | `.github/dependabot.yml`, Maven/npm lock data, SBOM |
| Secret management | Azure OIDC workflow, Key Vault Terraform target, `.gitignore`, Gitleaks CI |
| Infrastructure governance | `infrastructure/terraform/`, container definitions, environment-specific deployment workflow |
| Data minimization | browser-side transcript parsing and explicit consent records |
| Resilience | health endpoints and operations/DR documentation; production implementation/evidence remains required |

See `soc2-iso27001-readiness.md` for the gap register, P0 blockers, evidence requirements, and audit-readiness sequence.
