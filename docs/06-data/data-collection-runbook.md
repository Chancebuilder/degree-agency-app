# Data collection runbook

1. Open the institution’s current catalog or tuition page.
2. Record the exact figure, catalog year, and URL.
3. Add or update a row in the matching CSV under `/data/seed/`.
4. Set `last_verified_at` to today, `verified_by` to your name, `verification_status` to VERIFIED.
5. Re-run the API so `SeedLoader` upserts by stable UUID.
6. Never invent an equivalency. If the school does not publish the mapping, leave it out or mark it for admin review — never student-facing POSSIBLE.
