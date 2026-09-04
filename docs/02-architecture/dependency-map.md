# Dependency map

UI -> REST `/api/v1` -> application services -> JPA repositories -> PostgreSQL. Matching, staleness, and simulator engines are pure Java and have no Spring dependency.
