package com.perfumeria.exception;

public class StockInsuficienteException extends RuntimeException {
    
    public StockInsuficienteException(String producto, int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para '%s'. Disponible: %d, Solicitado: %d", 
                producto, stockDisponible, cantidadSolicitada));
    }
    
    public StockInsuficienteException(String message) {
        super(message);
    }
}
