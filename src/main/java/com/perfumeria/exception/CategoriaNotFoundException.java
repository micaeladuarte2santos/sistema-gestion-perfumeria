package com.perfumeria.exception;

public class CategoriaNotFoundException extends RuntimeException {
    
    public CategoriaNotFoundException(Long id) {
        super("Categoría no encontrada con ID: " + id);
    }
    
    public CategoriaNotFoundException(String message) {
        super(message);
    }
    
}
