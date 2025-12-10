package com.perfumeria.exception;

public class CategoriaAlreadyExistsException extends RuntimeException {
    public CategoriaAlreadyExistsException(String nombre) {
        super("Ya existe una categoría con el nombre: " + nombre);
    }
}
