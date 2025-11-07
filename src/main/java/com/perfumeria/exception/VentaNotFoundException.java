package com.perfumeria.exception;

public class VentaNotFoundException extends RuntimeException {
    
    public VentaNotFoundException(Long id) {
        super("Venta no encontrada con ID: " + id);
    }
    
    public VentaNotFoundException(String message) {
        super(message);
    }
    
}
