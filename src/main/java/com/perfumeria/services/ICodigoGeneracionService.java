package com.perfumeria.services;

public interface ICodigoGeneracionService {
    void eliminarCodigosPorUsuario(String username);
    String generarCodigoVerificacion(String username);
}
