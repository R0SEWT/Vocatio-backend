package com.acme.vocatio.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(description = "Correo electrónico único del usuario", example = "ada.lovelace@example.com")
        @NotBlank(message = "El email es obligatorio")
                @Email(message = "Ingresa un email válido")
                String email,
        @Schema(description = "Contraseña que cumple con las políticas de seguridad", example = "Cl4veSegura")
        @NotBlank(message = "La contraseña es obligatoria")
                @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
                @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe incluir al menos una letra mayúscula")
                @Pattern(regexp = ".*[0-9].*", message = "La contraseña debe incluir al menos un número")
                String password,
        @Schema(description = "Indica si se debe mantener la sesión abierta mediante refresh tokens", example = "true")
        boolean rememberMe) {

    //public boolean rememberMe() {
    //    return Boolean.TRUE.equals(rememberMe);
    //}
}
