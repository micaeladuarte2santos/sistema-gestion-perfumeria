package com.perfumeria.services.impl;

import com.perfumeria.models.CodigoVerificacion;
import com.perfumeria.repositories.CodigoVerificacionRepository;
import com.perfumeria.services.ICodigoGeneracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CodigoGeneracionServiceImpl implements ICodigoGeneracionService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final CodigoVerificacionRepository codigoVerificacionRepository;

    @Value("${verificacion.codigo.expiracion.minutos:15}")
    private int minutosExpiracion;

    @Override
    public void eliminarCodigosPorUsuario(String username) {
        codigoVerificacionRepository.deleteByUsername(username);
    }

    @Override
    public String generarCodigoVerificacion(String username) {
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));

        CodigoVerificacion codigoVerificacion = new CodigoVerificacion();
        codigoVerificacion.setUsername(username);
        codigoVerificacion.setCodigo(codigo);
        codigoVerificacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion));
        codigoVerificacion.setUsado(false);

        codigoVerificacionRepository.save(codigoVerificacion);
        return codigo;
    }
}
