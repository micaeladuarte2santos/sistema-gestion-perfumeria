package com.perfumeria.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.dto.DetalleVentaResponseDTO;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DetalleVentaMapperTest {

    @Test
    void toEntity_and_toResponse() {
        DetalleVentaMapper mapper = new DetalleVentaMapper();
        ReflectionTestUtils.setField(mapper, "productoMapper", new ProductoMapper());

        DetalleVentaRequestDTO req = new DetalleVentaRequestDTO();
        req.setCantidad(2);
        req.setProductoId(7L);

        DetalleVenta detalle = mapper.toEntity(req);
        assertNotNull(detalle);
        assertEquals(2, detalle.getCantidad());

        Producto p = new Producto();
        p.setId(7L);
        p.setNombre("Perfume X");
        detalle.setProducto(p);
        detalle.setId(11L);
        detalle.setSubtotal(300.0);

        DetalleVentaResponseDTO resp = mapper.toResponse(detalle);
        assertEquals(11L, resp.getId());
        assertEquals(2, resp.getCantidad());
        assertEquals(300.0, resp.getSubtotal());
        assertNotNull(resp.getProducto());
    }
}
