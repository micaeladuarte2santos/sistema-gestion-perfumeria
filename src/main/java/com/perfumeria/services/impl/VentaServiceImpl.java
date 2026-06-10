package com.perfumeria.services.impl;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.exception.ProductoNotFoundException;
import com.perfumeria.exception.StockInsuficienteException;
import com.perfumeria.exception.VentaNotFoundException;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;
import com.perfumeria.repositories.DetalleVentaRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.repositories.VentaRepository;
import com.perfumeria.services.IVentaService;
import com.perfumeria.models.EstadoVentaEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Override
    public List<Venta> findByDia(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    @Override
    @Transactional
    public Venta createVenta(VentaRequestDTO request) {

        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setEstado(EstadoVentaEnum.PENDIENTE);
        venta.setNombreCliente(request.getNombreCliente());
        venta.setMetodoPago(request.getMetodoPago());

        double totalVenta = 0.0;

        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVentaRequestDTO dvDTO : request.getDetalles()) {

            Producto producto = productoRepository.findById(dvDTO.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException(dvDTO.getProductoId()));

            if (producto.getStock() < dvDTO.getCantidad()) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        producto.getStock(),
                        dvDTO.getCantidad()
                );
            }

            producto.setStock(producto.getStock() - dvDTO.getCantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(dvDTO.getCantidad());
            detalle.setSubtotal(producto.getPrecio() * dvDTO.getCantidad());
            detalle.setVenta(ventaGuardada);

            detalleVentaRepository.save(detalle);

            totalVenta += detalle.getSubtotal();
        }

        ventaGuardada.setTotal(totalVenta);
        return ventaRepository.save(ventaGuardada);
    }

    @Transactional
    public Venta updateVenta(Long id, VentaRequestDTO request) {
        // 1. Buscar la venta con sus detalles (Eager fetch recomendado aquí)
        Venta venta = ventaRepository.findByIdConDetalles(id)
                .orElseThrow(() -> new VentaNotFoundException(id));

        // 2. Actualizar datos básicos
        venta.setNombreCliente(request.getNombreCliente());
        venta.setMetodoPago(request.getMetodoPago());
        if (request.getEstado() != null) {
            venta.setEstado(request.getEstado());
        }

        // 3. Asegurar lista inicializada
        if (venta.getDetalles() == null) {
            venta.setDetalles(new ArrayList<>());
        }

        // 4. Restaurar stock de los productos de la venta ANTERIOR
        for (DetalleVenta dv : venta.getDetalles()) {
            if (dv.getProducto() != null) {
                Producto p = dv.getProducto(); // No necesitas buscarlo de nuevo si ya viene en el detalle
                p.setStock(p.getStock() + dv.getCantidad());
                productoRepository.save(p);
            }
        }

        // 5. Limpiar detalles viejos
        // IMPORTANTE: Si usas orphanRemoval = true en la entidad Venta, 
        // solo necesitas hacer venta.getDetalles().clear()
        detalleVentaRepository.deleteAll(venta.getDetalles());
        venta.getDetalles().clear();

        // 6. Procesar nuevos detalles y CALCULAR TOTAL
        double acumuladorTotal = 0.0; // Cambié el nombre para mayor claridad

        for (DetalleVentaRequestDTO dvDTO : request.getDetalles()) {
            Producto producto = productoRepository.findById(dvDTO.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException(dvDTO.getProductoId()));

            if (producto.getStock() < dvDTO.getCantidad()) {
                throw new StockInsuficienteException(
                        producto.getNombre(),
                        producto.getStock(),
                        dvDTO.getCantidad()
                );
            }

            // Descontar nuevo stock
            producto.setStock(producto.getStock() - dvDTO.getCantidad());
            productoRepository.save(producto);

            // Crear nuevo detalle
            double subtotal = producto.getPrecio() * dvDTO.getCantidad();
            
            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(producto);
            dv.setCantidad(dvDTO.getCantidad());
            dv.setSubtotal(subtotal);
            dv.setVenta(venta);

            venta.getDetalles().add(dv);
            
            // 🔥 CORRECCIÓN AQUÍ: Sumar al acumulador
            acumuladorTotal += subtotal;
        }

        // 7. Asignar el total calculado y guardar
        venta.setTotal(acumuladorTotal);
        
        // Si tienes CascadeType.ALL en la entidad Venta, no necesitas saveAll de detalles
        detalleVentaRepository.saveAll(venta.getDetalles());

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNotFoundException(id));

        if (venta.getDetalles() != null) {
            for (DetalleVenta dv : venta.getDetalles()) {
                Producto p = productoRepository.findById(dv.getProducto().getId())
                        .orElseThrow(() -> new ProductoNotFoundException(dv.getProducto().getId()));
                p.setStock(p.getStock() + dv.getCantidad());
                productoRepository.save(p);
            }
        }

        ventaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Venta actualizarEstado(Long id, EstadoVentaEnum nuevoEstado) {

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNotFoundException(id));

        venta.setEstado(nuevoEstado);

        return ventaRepository.save(venta);
    }
}