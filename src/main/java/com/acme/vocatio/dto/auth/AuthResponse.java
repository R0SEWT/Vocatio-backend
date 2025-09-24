package com.acme.vocatio.dto.auth;

import java.time.Instant;

public record AuthResponse(
        String message,
        UserSummary user,
        TokenBundle tokens) {

    public record UserSummary(Long id, String email) {}

    public record TokenBundle(
            String tokenType,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt) {}
}
