package com.degreedean.identity;

import com.degreedean.common.ApiException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationProvider authenticationProvider;
    private final UserAccountRepository users;

    public AuthController(AuthenticationProvider authenticationProvider, UserAccountRepository users) {
        this.authenticationProvider = authenticationProvider;
        this.users = users;
    }

    @PostMapping("/register")
    public AuthTokens register(@Valid @RequestBody RegisterRequest request) {
        return authenticationProvider.register(request.email(), request.password());
    }

    @PostMapping("/login")
    public AuthTokens login(@Valid @RequestBody LoginRequest request) {
        return authenticationProvider.authenticate(request.email(), request.password());
    }

    @PostMapping("/refresh")
    public AuthTokens refresh(@Valid @RequestBody RefreshRequest request) {
        return authenticationProvider.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public Map<String, String> logout(@RequestBody RefreshRequest request) {
        if (request != null && request.refreshToken() != null) {
            authenticationProvider.logout(request.refreshToken());
        }
        return Map.of("status", "ok");
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserAccount user = users.findById(userId).orElseThrow(() -> ApiException.unauthorized("Unknown user"));
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "tier", user.getTier()
        );
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 10) String password
    ) {}

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

    public record RefreshRequest(String refreshToken) {}
}
