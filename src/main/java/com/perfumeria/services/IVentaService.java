package com.perfumeria.services;

import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {

    Venta createVenta(VentaRequestDTO request);
    Venta findById(Long id);
    List<Venta> findAll();
    void deleteById(Long id);
    Venta actualizarEstado(Long id, EstadoVentaEnum nuevoEstado);
    Venta updateVenta(Long id, VentaRequestDTO ventaActualizada);
    List<Venta> findByDia(LocalDate fecha);
}
