# Module boundaries

The MVP ships as one Spring Boot application. Each directory here documents a boundary that can later become its own service. Implementation lives under `apps/api/src/main/java/com/degreedean/<module>`.

| Module | Class | Notes |
|---|---|---|
| identity | 1 | AuthenticationProvider, Argon2id, JWT |
| student-profile | 1 | Age gate + working profile |
| consent | 1 | Purpose-specific records |
| academic-wallet | 1 | Structured assets only |
| transcripts | 1 | Browser parser + server validation |
| institutions | 1 | Tier A catalog |
| programs | 1 | Requirements as slots |
| credit-catalog | 1 | Providers and opportunities |
| policies | 1 | Versioned rules as data |
| equivalencies | 1 | Table-lookup matching |
| degree-planning | 1 | Goals, plans, matches |
| cost-engine | 1 | Embedded in planning + simulator |
| completion-simulator | 1 | Config-driven math |
| recommendations | 1 | Next Best Action only |
| audit | 1 | Policy and equivalency changes |
| payments | 2 | Schema + stub port |
| prior-learning | 2 | Schema + stub port |
| admin-console | 2 | Read-only plus policy/equivalency CRUD |
