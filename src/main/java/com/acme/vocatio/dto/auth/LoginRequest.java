package com.acme.vocatio.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
                @Email(message = "Ingresa un email válido")
                String email,
        @NotBlank(message = "La contraseña es obligatoria") String password,
        Boolean rememberMe) {

    //public boolean rememberMe() {
    //    return Boolean.TRUE.equals(rememberMe);
    //}
}
