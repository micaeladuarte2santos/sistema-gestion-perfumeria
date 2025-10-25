package com.perfumeria.services;

import com.perfumeria.models.Venta;
import java.util.List;

public interface IVentaService {

    Venta createVenta(Venta venta);
    Venta findById(Long id);
    List<Venta> findAll();
    void deleteById(Long id);
}
