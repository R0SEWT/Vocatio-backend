package com.acme.vocatio.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Correo electrónico registrado", example = "ada.lovelace@example.com")
        @NotBlank(message = "El email es obligatorio")
                @Email(message = "Ingresa un email válido")
                String email,
        @Schema(description = "Contraseña asociada a la cuenta", example = "Cl4veSegura")
        @NotBlank(message = "La contraseña es obligatoria") String password,
        @Schema(description = "Solicita mantener la sesión activa mediante refresh tokens", example = "true")
        Boolean rememberMe) {

    //public boolean rememberMe() {
    //    return Boolean.TRUE.equals(rememberMe);
    //}
}
