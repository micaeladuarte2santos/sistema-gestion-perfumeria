package com.perfumeria.exception;

public class UsuarioEmailAlreadyExistsException extends RuntimeException {
    public UsuarioEmailAlreadyExistsException(String email) {
        super("Ya existe un usuario con el email: " + email);
    }
}
