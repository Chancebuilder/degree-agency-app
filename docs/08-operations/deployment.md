# Deployment

Local: Postgres + `mvn spring-boot:run` + `npm run dev`.
Azure: GitHub Actions builds images, pushes ACR, revisions Container Apps. Developers do not deploy laptops to production.
