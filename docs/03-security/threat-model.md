# Threat model

Spoofed transcript rows, stale policy presented as current, over-privileged admin, token theft, IDOR on plans, supply-chain dependency risk. Mitigations: student verification + server validation, staleness caps, admin role, hashed refresh, user-scoped queries, CI scans.
