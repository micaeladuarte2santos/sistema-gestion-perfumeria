package com.perfumeria.services;

import com.perfumeria.models.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaReporteService {
    List<Venta> findByMes(int mes, int anio);
    List<Venta> findByAnio(int anio);
    Double getRecaudacionPorDia(LocalDate fecha);
    Double getRecaudacionPorMes(int mes, int anio);
    Double getRecaudacionPorAnio(int anio);
    Long getCantidadDevolucionesDia(LocalDate fecha);
    Double getTotalDevolucionesDia(LocalDate fecha);
    Long getCantidadDevolucionesMes(int mes, int anio);
    Double getTotalDevolucionesMes(int mes, int anio);
    Long getCantidadDevolucionesAnio(int anio);
    Double getTotalDevolucionesAnio(int anio);
}
