package com.acme.vocatio.exception;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(String message) {
        super(message);
    }
    
    public ProfileNotFoundException(Long userId) {
        super("Perfil para el usuario con ID " + userId + " no encontrado");
    }
}