package com.degreedean.identity;

public interface AuthenticationProvider {
    AuthTokens register(String email, String password);
    AuthTokens authenticate(String email, String password);
    AuthTokens refresh(String refreshToken);
    void logout(String refreshToken);
}
