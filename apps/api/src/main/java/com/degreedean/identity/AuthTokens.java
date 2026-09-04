package com.degreedean.identity;

import java.util.UUID;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        UUID userId,
        String email,
        String role,
        String tier
) {}
