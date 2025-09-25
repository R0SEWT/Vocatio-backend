package com.acme.vocatio.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio")
                @Email(message = "Ingresa un email válido")
                String email,
        @NotBlank(message = "La contraseña es obligatoria")
                @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
                @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe incluir al menos una letra mayúscula")
                @Pattern(regexp = ".*[0-9].*", message = "La contraseña debe incluir al menos un número")
                String password,
        boolean rememberMe) {

    //public boolean rememberMe() {
    //    return Boolean.TRUE.equals(rememberMe);
    //}
}
