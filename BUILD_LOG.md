# Build log

## Scope contract (Section 1)

- MVP is done when one student finishes the Section 41 journey against seeded data.
- Class 1 is fully implemented: identity through audit, including CSV/TSV/TXT parsing and Next Best Action only.
- Class 2 is schema + interface + documented stub: prior-learning, payments, admin (read + policy/equivalency CRUD + stale queue), marketplace visibility fields.
- Class 3 has no tables: pre-health, Caribbean pathways, PDF/OCR, Stripe, any AI.
- Auth is email/password Argon2id behind `AuthenticationProvider`.
- Transcript parsing is in the browser; the API re-validates reviewed rows.
- Tiers are Free / Plus / Pro.
- Policy and equivalency are data, never Java constants.
- Matching is table lookup. Staleness degrades claims.
- Seed data is the product risk: 3 institutions, 6 programs, 4 providers, 69 opportunities, 459 equivalencies.

## Highest-risk items

1. Seed-data acquisition and ongoing verification
2. Equivalencies compiled from public transfer posture rather than a registrar extract
3. UMPI Cybersecurity YourPace timing (program expanding; requirement list compiled)
4. Stale tuition silently becoming a cheapest-path claim — mitigated by Section 18
5. Client-parsed transcript rows being trusted — mitigated by server validation
6. JWT secret handling across environments
7. Free-tier entitlement enforcement as the product grows
8. Simulator defaults substituting for observed effort data
9. Azure OIDC deploy remaining a template until subscriptions exist
10. No Docker in this cloud environment; local Postgres is used instead

## Classification notes

No module was reclassified. Admin includes the stale-record queue because Section 18.2 makes it load-bearing for data honesty.
