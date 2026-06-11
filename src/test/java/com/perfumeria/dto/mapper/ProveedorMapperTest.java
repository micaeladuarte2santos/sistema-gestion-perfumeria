package com.perfumeria.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.perfumeria.dto.ProveedorRequestDTO;
import com.perfumeria.dto.ProveedorResponseDTO;
import com.perfumeria.models.Proveedor;
import org.junit.jupiter.api.Test;

class ProveedorMapperTest {

    private final ProveedorMapper mapper = new ProveedorMapper();

    @Test
    void toEntity_and_toResponse() {
        ProveedorRequestDTO req = new ProveedorRequestDTO();
        req.setNombre("Proveedor A");
        req.setTelefono("1234");
        req.setEmail("prov@example.com");

        Proveedor p = mapper.toEntity(req);
        assertEquals("Proveedor A", p.getNombre());

        p.setId(9L);
        p.setActivo(true);

        ProveedorResponseDTO resp = mapper.toResponse(p);
        assertEquals(9L, resp.getId());
        assertEquals("prov@example.com", resp.getEmail());
    }
}
