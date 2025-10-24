package com.perfumeria.services;

import com.perfumeria.models.Venta;
import java.util.List;
import java.util.Optional;

public interface IVentaService {

    Venta createVenta(Venta venta);
    Optional<Venta> findById(Long id);
    List<Venta> findAll();
    void deleteById(Long id);
}
