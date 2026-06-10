package com.perfumeria.services.impl;

import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.Venta;
import com.perfumeria.repositories.VentaRepository;
import com.perfumeria.services.IVentaReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VentaReporteServiceImpl implements IVentaReporteService {

    private final VentaRepository ventaRepository;

    @Override
    public List<Venta> findByMes(int mes, int anio) {
        return ventaRepository.findByMes(mes, anio);
    }

    @Override
    public List<Venta> findByAnio(int anio) {
        return ventaRepository.findByAnio(anio);
    }

    @Override
    public Double getRecaudacionPorDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.getRecaudacionPorDia(inicio, fin)).orElse(0.0);
    }

    @Override
    public Double getRecaudacionPorMes(int mes, int anio) {
        return Optional.ofNullable(ventaRepository.getRecaudacionPorMes(mes, anio)).orElse(0.0);
    }

    @Override
    public Double getRecaudacionPorAnio(int anio) {
        return Optional.ofNullable(ventaRepository.getRecaudacionPorAnio(anio)).orElse(0.0);
    }

    @Override
    public Long getCantidadDevolucionesDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.countDevolucionesDia(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0L);
    }

    @Override
    public Double getTotalDevolucionesDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.totalDevolucionesDia(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0.0);
    }

    @Override
    public Long getCantidadDevolucionesMes(int mes, int anio) {
        LocalDate inicioFecha = LocalDate.of(anio, mes, 1);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = inicioFecha.plusMonths(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.countDevolucionesMes(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0L);
    }

    @Override
    public Double getTotalDevolucionesMes(int mes, int anio) {
        LocalDate inicioFecha = LocalDate.of(anio, mes, 1);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = inicioFecha.plusMonths(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.totalDevolucionesMes(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0.0);
    }

    @Override
    public Long getCantidadDevolucionesAnio(int anio) {
        LocalDate inicioFecha = LocalDate.of(anio, 1, 1);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = inicioFecha.plusYears(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.countDevolucionesAnio(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0L);
    }

    @Override
    public Double getTotalDevolucionesAnio(int anio) {
        LocalDate inicioFecha = LocalDate.of(anio, 1, 1);
        LocalDateTime inicio = inicioFecha.atStartOfDay();
        LocalDateTime fin = inicioFecha.plusYears(1).atStartOfDay();
        return Optional.ofNullable(ventaRepository.totalDevolucionesAnio(inicio, fin, EstadoVentaEnum.DEVUELTA)).orElse(0.0);
    }
}
