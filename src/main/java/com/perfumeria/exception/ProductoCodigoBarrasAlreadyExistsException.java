package com.perfumeria.exception;

public class ProductoCodigoBarrasAlreadyExistsException extends RuntimeException {
    
    public ProductoCodigoBarrasAlreadyExistsException(String codigoBarras) {
        super("Ya existe un producto con el código de barras: " + codigoBarras);
    }
    
}
