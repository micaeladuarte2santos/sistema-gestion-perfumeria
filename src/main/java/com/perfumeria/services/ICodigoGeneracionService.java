package com.perfumeria.services;

public interface CodigoGeneracionService {
    void eliminarCodigosPorUsuario(String username);
    String generarCodigoVerificacion(String username);
}
