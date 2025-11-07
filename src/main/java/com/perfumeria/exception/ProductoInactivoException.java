package com.perfumeria.exception;

public class ProductoInactivoException extends RuntimeException {
    
    public ProductoInactivoException(Long id) {
        super("El producto con ID " + id + " está inactivo y no puede ser utilizado");
    }
    
    public ProductoInactivoException(String message) {
        super(message);
    }
}
