package com.perfumeria.exception;

public class UsuarioAlreadyVerifiedException extends RuntimeException {

    public UsuarioAlreadyVerifiedException(String username) {
        super("El usuario ya está verificado: " + username);
    }
}
