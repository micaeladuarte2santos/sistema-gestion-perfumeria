package com.perfumeria.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DetalleVentaRepositoryTest {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void findByVentaId_and_findByProductoId_work() {
        Venta v = new Venta();
        v.setNombreCliente("C");
        v.setFecha(LocalDateTime.now());
        v.setTotal(10.0);
        v.setMetodoPago(com.perfumeria.models.MetodoPagoEnum.EFECTIVO);
        ventaRepository.save(v);

        Producto p = new Producto();
        p.setNombre("P");
        productoRepository.save(p);

        DetalleVenta d = new DetalleVenta();
        d.setVenta(v);
        d.setProducto(p);
        d.setCantidad(1);
        d.setSubtotal(10.0);
        detalleVentaRepository.save(d);

        List<DetalleVenta> byVenta = detalleVentaRepository.findByVentaId(v.getId());
        assertTrue(byVenta.size() >= 1);

        List<DetalleVenta> byProducto = detalleVentaRepository.findByProductoId(p.getId());
        assertTrue(byProducto.size() >= 1);
    }
}
