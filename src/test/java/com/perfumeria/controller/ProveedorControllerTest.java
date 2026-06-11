package com.perfumeria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfumeria.dto.ProveedorRequestDTO;
import com.perfumeria.dto.mapper.ProveedorMapper;
import com.perfumeria.models.Proveedor;
import com.perfumeria.services.IProveedorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProveedorController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProveedorService proveedorService;

    @MockBean
    private ProveedorMapper proveedorMapper;

    @Test
    void agregarProveedor_devuelve201() throws Exception {
        ProveedorRequestDTO req = new ProveedorRequestDTO();
        req.setNombre("Prov A");

        Proveedor p = new Proveedor();
        p.setId(2L);
        p.setNombre("Prov A");

        when(proveedorMapper.toEntity(any(ProveedorRequestDTO.class))).thenReturn(p);
        when(proveedorService.agregarProveedor(any(Proveedor.class))).thenReturn(p);
        when(proveedorMapper.toResponse(any(Proveedor.class))).thenReturn(new com.perfumeria.dto.ProveedorResponseDTO(2L,"Prov A",null,null,true));

        mockMvc.perform(post("/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2));
    }
}
