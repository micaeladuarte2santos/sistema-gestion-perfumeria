package com.perfumeria.services;

import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {

    Venta createVenta(Venta venta);
    Venta findById(Long id);
    List<Venta> findAll();
    void deleteById(Long id);
    List<Venta> findByMes(int mes, int anio);
    List<Venta> findByAnio(int anio);
    Double getRecaudacionPorDia(LocalDate fecha);
    Double getRecaudacionPorMes(int mes, int anio);
    Double getRecaudacionPorAnio(int anio);
    Venta actualizarEstado(Long id, EstadoVentaEnum nuevoEstado);
    Venta updateVenta(Long id, Venta ventaActualizada);
}
