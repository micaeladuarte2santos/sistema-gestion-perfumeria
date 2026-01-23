package com.perfumeria.exception;

public class CodigoVerificacionInvalidoException extends RuntimeException {
    public CodigoVerificacionInvalidoException() {
        super("El código de verificación es inválido");
    }
}
