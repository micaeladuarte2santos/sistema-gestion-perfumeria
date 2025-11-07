package com.perfumeria.exception;

public class UsuarioAlreadyExistsException extends RuntimeException {
    
    public UsuarioAlreadyExistsException(String username) {
        super("El usuario ya existe: " + username);
    }
    

}
