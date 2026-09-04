# Runbook

API down: check `/actuator/health`. Seed missing: confirm `APP_SEED_PATH`. Auth failures: check JWT secret consistency across replicas. Stale queue growing: run the data-collection runbook.
