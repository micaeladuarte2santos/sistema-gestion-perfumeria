package com.perfumeria.exception;

public class MensajeError {
 
	private String mensaje;
	
	public MensajeError() {
		
	}
	public MensajeError(String mensaje) {
		this.mensaje=mensaje;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
}

