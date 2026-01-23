package com.perfumeria.exception;

public class CodigoVerificacionExpiradoException extends RuntimeException {
    public CodigoVerificacionExpiradoException() {
        super("El código de verificación ha expirado. Por favor solicita un nuevo código");
    }
}
