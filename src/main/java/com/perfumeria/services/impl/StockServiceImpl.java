package com.perfumeria.services.impl;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.exception.ProductoNotFoundException;
import com.perfumeria.exception.StockInsuficienteException;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.services.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public Map<Long, Producto> descontarStockPorDetalles(List<DetalleVentaRequestDTO> detalles) {
        Map<Long, Producto> productosActualizados = new LinkedHashMap<>();

        for (DetalleVentaRequestDTO detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException(detalle.getProductoId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        producto.getStock(),
                        detalle.getCantidad()
                );
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
            productosActualizados.put(producto.getId(), producto);
        }

        return productosActualizados;
    }

    @Override
    @Transactional
    public void restaurarStockParaDetalles(List<DetalleVenta> detalles) {
        if (detalles == null) {
            return;
        }

        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto() == null) {
                continue;
            }

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new ProductoNotFoundException(detalle.getProducto().getId()));

            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
    }
}
