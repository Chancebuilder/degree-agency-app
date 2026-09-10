package com.degreedean.identity;

import com.degreedean.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final int MINIMUM_HMAC_KEY_BYTES = 32;
    private final AppProperties properties;

    public JwtService(AppProperties properties) {
        this.properties = properties;
        validateSecret(properties.getJwt().getSecret());
    }

    public String issueAccessToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getJwt().getAccessMinutes() * 60L);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("degree-agency-api")
                .subject(user.getId().toString())
                .claim("role", user.getRole())
                .claim("tier", user.getTier())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .requireIssuer("degree-agency-api")
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID userId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private static void validateSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("APP_JWT_SECRET must be configured");
        }
        int length = secret.getBytes(StandardCharsets.UTF_8).length;
        if (length < MINIMUM_HMAC_KEY_BYTES) {
            throw new IllegalStateException("APP_JWT_SECRET must contain at least 32 bytes of entropy");
        }
    }
}
