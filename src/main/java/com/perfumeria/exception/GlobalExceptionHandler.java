package com.perfumeria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
		CategoriaNotFoundException.class,
		CategoriaAlreadyExistsException.class,
		ProductoNotFoundException.class,
		UsuarioNotFoundException.class,
        VentaInvalidaException.class,
        StockInsuficienteException.class,
        ProductoInactivoException.class,
        ProveedorNotFoundException.class,
		UsuarioAlreadyExistsException.class,
		UsuarioEmailAlreadyExistsException.class,
		ProductoCodigoBarrasAlreadyExistsException.class,
        VentaNotFoundException.class,
        CodigoVerificacionInvalidoException.class,
        CodigoVerificacionExpiradoException.class
	})
	public ResponseEntity<MensajeError> manejarErroresDeValidacion(RuntimeException ex) {
		return new ResponseEntity<>(new MensajeError(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
}
