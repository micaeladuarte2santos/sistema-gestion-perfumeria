package com.perfumeria.exception;

public class ProveedorEmailAlreadyExistsException extends RuntimeException {

    public ProveedorEmailAlreadyExistsException(String email) {
        super("Ya existe un proveedor con el email " + email);
    }
}
