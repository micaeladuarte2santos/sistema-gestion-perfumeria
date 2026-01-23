package com.perfumeria.services;

public interface IEmailService {
    void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario);
}
