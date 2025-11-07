package com.perfumeria.exception;

public class ProveedorNotFoundException extends RuntimeException {
    
    public ProveedorNotFoundException(Long id) {
        super("Proveedor no encontrado con ID: " + id);
    }
    
    public ProveedorNotFoundException(String message) {
        super(message);
    }
    
}
