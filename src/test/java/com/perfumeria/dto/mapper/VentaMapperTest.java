package com.perfumeria.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class VentaMapperTest {

    @Test
    void toResponse_and_toTicketText() {
        VentaMapper mapper = new VentaMapper();
        DetalleVentaMapper detalleMapper = new DetalleVentaMapper();
        ReflectionTestUtils.setField(detalleMapper, "productoMapper", new ProductoMapper());
        ReflectionTestUtils.setField(mapper, "detalleVentaMapper", detalleMapper);
        ReflectionTestUtils.setField(mapper, "ticketEmpresaNombre", "MiEmpresa");
        ReflectionTestUtils.setField(mapper, "ticketEmpresaCuil", "20-12345678-9");

        Venta venta = new Venta();
        venta.setId(1L);
        venta.setNombreCliente("Cliente Test");
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(250.0);

        Producto p = new Producto();
        p.setId(3L);
        p.setNombre("Perfume Test");
        p.setPrecio(125.0);

        DetalleVenta d = new DetalleVenta();
        d.setId(5L);
        d.setProducto(p);
        d.setCantidad(2);
        d.setSubtotal(250.0);

        venta.setDetalles(List.of(d));

        var resp = mapper.toResponse(venta);
        assertEquals(1L, resp.getId());
        assertEquals("Cliente Test", resp.getNombreCliente());
        assertTrue(resp.getDetalles().size() == 1);

        String ticket = mapper.toTicketText(venta);
        assertTrue(ticket.contains("MiEmpresa"));
        assertEquals(1, resp.getDetalles().size());
    }
}
