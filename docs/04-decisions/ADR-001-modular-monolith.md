# ADR-001 Modular monolith

Context: MVP must ship as one deployable without blocking later extraction.
Decision: Single Spring Boot app, package-per-module.
Alternatives: microservices, modulith build plugin.
Consequences: simpler ops now; module APIs stay narrow so extraction remains possible.
