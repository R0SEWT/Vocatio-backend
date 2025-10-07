package com.acme.vocatio.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(name = "AuthResponse", description = "Respuesta estándar para el registro e inicio de sesión")
public record AuthResponse(
        @Schema(description = "Mensaje contextual del proceso de autenticación", example = "Registro exitoso")
        String message,
        @Schema(description = "Datos mínimos del usuario autenticado")
        UserSummary user,
        @Schema(description = "Tokens emitidos para manejar la sesión del usuario")
        TokenBundle tokens) {

    @Schema(name = "AuthResponse.UserSummary", description = "Información básica del usuario autenticado")
    public record UserSummary(
            @Schema(description = "Identificador interno del usuario", example = "42") Long id,
            @Schema(description = "Correo electrónico del usuario", example = "ada.lovelace@example.com") String email) {
    }

    @Schema(name = "AuthResponse.TokenBundle", description = "Tokens devueltos al autenticarse")
    public record TokenBundle(
            @Schema(description = "Tipo del token de acceso", example = "Bearer")
            String tokenType,
            @Schema(description = "JWT de acceso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String accessToken,
            @Schema(description = "Fecha de expiración del token de acceso", example = "2024-07-12T21:45:00Z")
            Instant accessTokenExpiresAt,
            @Schema(description = "Refresh token emitido cuando corresponde", example = "def50200c9b2...")
            String refreshToken,
            @Schema(description = "Fecha de expiración del refresh token", example = "2024-07-26T21:45:00Z")
            Instant refreshTokenExpiresAt) {
    }
}
