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
    private final AppProperties properties;

    public JwtService(AppProperties properties) {
        this.properties = properties;
    }

    public String issueAccessToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(properties.getJwt().getAccessMinutes() * 60L);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("tier", user.getTier())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public UUID userId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    private SecretKey key() {
        byte[] bytes = properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            bytes = java.util.Arrays.copyOf(bytes, 32);
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}
