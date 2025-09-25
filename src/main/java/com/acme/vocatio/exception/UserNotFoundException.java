package com.acme.vocatio.exception;

/** Se lanza cuando no existe el usuario solicitado. */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("Usuario con id " + userId + " no encontrado");
    }
}
