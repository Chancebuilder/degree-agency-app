# Security

Report vulnerabilities to the repository owners. Do not file public issues that include exploit details.

## Defaults

- Passwords are hashed with Argon2id
- Access tokens are short-lived JWTs
- Refresh tokens rotate and are stored hashed
- Original transcripts are not uploaded
- Admin routes require `ROLE_ADMIN`
- Secrets belong in environment variables or Azure Key Vault

The seeded admin password is for local development only.
