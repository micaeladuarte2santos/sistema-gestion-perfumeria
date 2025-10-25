package com.perfumeria.services.impl;

import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;
import com.perfumeria.repositories.DetalleVentaRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.repositories.VentaRepository;
import com.perfumeria.services.IVentaService;
import com.perfumeria.models.EstadoVenta;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VentaServiceImpl implements IVentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    @Transactional
    public Venta createVenta(Venta venta) {

        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVenta.PENDIENTE);
        
        double totalVenta = 0.0;
        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : venta.getDetalles()) {

            Producto producto = productoRepository.findById(detalle.getProducto().getId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // resta del stock
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            // calcula el subtotal
            double subtotal = producto.getPrecio() * detalle.getCantidad();
            detalle.setSubtotal(subtotal);
            detalle.setProducto(producto);
            detalle.setVenta(ventaGuardada);

            detalleVentaRepository.save(detalle);
            totalVenta += subtotal;
        }
        ventaGuardada.setTotal(totalVenta);
        ventaRepository.save(ventaGuardada);
        return ventaGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (venta.getDetalles() != null) {
            for (DetalleVenta dv : venta.getDetalles()) {
                Producto p = productoRepository.findById(dv.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado al eliminar venta"));
                p.setStock(p.getStock() + dv.getCantidad());
                productoRepository.save(p);
            }
        }

        ventaRepository.deleteById(id);
    }


}
