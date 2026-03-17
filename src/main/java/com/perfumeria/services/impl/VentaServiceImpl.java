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
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        venta.setEstado(EstadoVentaEnum.PENDIENTE);
        
        double totalVenta = 0.0;
        Venta ventaGuardada = ventaRepository.save(venta);

        for (DetalleVenta detalle : venta.getDetalles()) {

            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                .orElseThrow(() -> new ProductoNotFoundException(detalle.getProducto().getId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException(producto.getNombre(), producto.getStock(), detalle.getCantidad());
            }

            
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            
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

    @Transactional
    public Venta updateVenta(Long id, VentaRequestDTO request) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new VentaNotFoundException(id));

        venta.setNombreCliente(request.getNombreCliente());
        venta.setMetodoPago(request.getMetodoPago());
        if (request.getEstado() != null) {
            venta.setEstado(request.getEstado());
        }

        
        if (venta.getDetalles() != null) {
            for (DetalleVenta dv : venta.getDetalles()) {
                if (dv.getProducto() != null) { // ✅ protección contra null
                    Producto p = productoRepository.findById(dv.getProducto().getId())
                            .orElseThrow(() -> new ProductoNotFoundException(dv.getProducto().getId()));
                    p.setStock(p.getStock() + dv.getCantidad());
                    productoRepository.save(p);
                }
            }
            detalleVentaRepository.deleteAll(venta.getDetalles());
            venta.getDetalles().clear();
        }

        // 2️⃣ Agregar nuevos detalles
        double totalVenta = 0.0;
        for (DetalleVentaRequestDTO dvDTO : request.getDetalles()) {
            Producto producto = productoRepository.findById(dvDTO.getProductoId())
                    .orElseThrow(() -> new ProductoNotFoundException(dvDTO.getProductoId()));

            if (producto.getStock() < dvDTO.getCantidad()) {
                throw new StockInsuficienteException(producto.getNombre(), producto.getStock(), dvDTO.getCantidad());
            }

            producto.setStock(producto.getStock() - dvDTO.getCantidad());
            productoRepository.save(producto);

            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(producto);
            dv.setCantidad(dvDTO.getCantidad());
            dv.setSubtotal(producto.getPrecio() * dvDTO.getCantidad());
            dv.setVenta(venta);

            detalleVentaRepository.save(dv);
            venta.getDetalles().add(dv);

            totalVenta += dv.getSubtotal();
        }

        venta.setTotal(totalVenta);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElseThrow(() -> new VentaNotFoundException(id));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new VentaNotFoundException(id));

        if (venta.getDetalles() != null) {
            for (DetalleVenta dv : venta.getDetalles()) {
                Producto p = productoRepository.findById(dv.getProducto().getId()).orElseThrow(() -> new ProductoNotFoundException(dv.getProducto().getId()));
                p.setStock(p.getStock() + dv.getCantidad());
                productoRepository.save(p);
            }
        }

        ventaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByMes(int mes, int anio) {
        return ventaRepository.findByMes(mes, anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByAnio(int anio) {
        return ventaRepository.findByAnio(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRecaudacionPorDia(LocalDate fecha) {
        return Optional.ofNullable(ventaRepository.getRecaudacionPorDia(fecha)).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRecaudacionPorMes(int mes, int anio) {
        return Optional.ofNullable(ventaRepository.getRecaudacionPorMes(mes, anio)).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRecaudacionPorAnio(int anio) {
        return Optional.ofNullable(ventaRepository.getRecaudacionPorAnio(anio)).orElse(0.0);
    }

    @Override
    @Transactional
    public Venta actualizarEstado(Long id, EstadoVentaEnum nuevoEstado) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new VentaNotFoundException(id));
        venta.setEstado(nuevoEstado);
        return ventaRepository.save(venta);
    }

}
