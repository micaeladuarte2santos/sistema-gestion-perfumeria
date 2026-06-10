package com.perfumeria.services;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;

import java.util.List;
import java.util.Map;

public interface StockService {
    Map<Long, Producto> descontarStockPorDetalles(List<DetalleVentaRequestDTO> detalles);
    void restaurarStockParaDetalles(List<DetalleVenta> detalles);
}
