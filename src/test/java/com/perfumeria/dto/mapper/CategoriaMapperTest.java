package com.perfumeria.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.perfumeria.dto.CategoriaRequestDTO;
import com.perfumeria.dto.CategoriaResponseDTO;
import com.perfumeria.models.CategoriaProducto;
import org.junit.jupiter.api.Test;

class CategoriaMapperTest {

    private final CategoriaMapper mapper = new CategoriaMapper();

    @Test
    void toEntity_and_toResponse() {
        CategoriaRequestDTO req = new CategoriaRequestDTO();
        req.setNombre("Perfumes");

        CategoriaProducto cat = mapper.toEntity(req);
        assertEquals("Perfumes", cat.getNombre());

        cat.setId(12L);
        CategoriaResponseDTO resp = mapper.toResponse(cat);
        assertEquals(12L, resp.getId());
    }
}
