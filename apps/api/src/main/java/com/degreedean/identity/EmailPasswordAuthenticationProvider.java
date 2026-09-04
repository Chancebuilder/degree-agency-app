package com.degreedean.identity;

import com.degreedean.common.ApiException;
import com.degreedean.config.AppProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserAccountRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final JwtService jwtService;
    private final AppProperties properties;
    private final Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public EmailPasswordAuthenticationProvider(
            UserAccountRepository users,
            RefreshTokenRepository refreshTokens,
            JwtService jwtService,
            AppProperties properties
    ) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.jwtService = jwtService;
        this.properties = properties;
    }

    @Override
    @Transactional
    public AuthTokens register(String email, String password) {
        String normalized = email.trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(normalized)) {
            throw ApiException.conflict("An account with that email already exists");
        }
        if (password == null || password.length() < 10) {
            throw ApiException.badRequest("Password must be at least 10 characters");
        }
        Instant now = Instant.now();
        UserAccount user = new UserAccount();
        user.setId(UUID.randomUUID());
        user.setEmail(normalized);
        user.setPasswordHash(encoder.encode(password));
        user.setRole("STUDENT");
        user.setTier("FREE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        users.save(user);
        return issue(user);
    }

    @Override
    @Transactional
    public AuthTokens authenticate(String email, String password) {
        UserAccount user = users.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!encoder.matches(password, user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        return issue(user);
    }

    @Override
    @Transactional
    public AuthTokens refresh(String refreshToken) {
        String hash = hash(refreshToken);
        RefreshToken stored = refreshTokens.findByTokenHash(hash)
                .orElseThrow(() -> ApiException.unauthorized("Refresh token is invalid"));
        if (stored.getRevokedAt() != null || stored.getExpiresAt().isBefore(Instant.now())) {
            throw ApiException.unauthorized("Refresh token is expired");
        }
        stored.setRevokedAt(Instant.now());
        refreshTokens.save(stored);
        UserAccount user = users.findById(stored.getUserId())
                .orElseThrow(() -> ApiException.unauthorized("Account no longer exists"));
        return issue(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokens.findByTokenHash(hash(refreshToken)).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            refreshTokens.save(token);
        });
    }

    private AuthTokens issue(UserAccount user) {
        String access = jwtService.issueAccessToken(user);
        String refresh = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshToken token = new RefreshToken();
        token.setId(UUID.randomUUID());
        token.setUserId(user.getId());
        token.setTokenHash(hash(refresh));
        token.setExpiresAt(Instant.now().plusSeconds(properties.getJwt().getRefreshDays() * 86400L));
        token.setCreatedAt(Instant.now());
        refreshTokens.save(token);
        return new AuthTokens(
                access,
                refresh,
                properties.getJwt().getAccessMinutes() * 60L,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getTier()
        );
    }

    static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
