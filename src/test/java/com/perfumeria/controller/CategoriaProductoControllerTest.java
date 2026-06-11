package com.perfumeria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfumeria.dto.CategoriaRequestDTO;
import com.perfumeria.dto.CategoriaResponseDTO;
import com.perfumeria.dto.mapper.CategoriaMapper;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.services.impl.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoriaProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriaProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaMapper categoriaMapper;

    @MockBean
    private CategoriaServiceImpl categoriaService;

    @Test
    void crearCategoria_devuelve201() throws Exception {

        CategoriaRequestDTO req = new CategoriaRequestDTO();
        req.setNombre("Cat A");

        CategoriaProducto cat = new CategoriaProducto();
        cat.setId(3L);
        cat.setNombre("Cat A");

        when(categoriaMapper.toEntity(any(CategoriaRequestDTO.class)))
                .thenReturn(cat);

        when(categoriaService.crearCategoria(any(CategoriaProducto.class)))
                .thenReturn(cat);

        when(categoriaMapper.toResponse(any(CategoriaProducto.class)))
                .thenReturn(new CategoriaResponseDTO(3L, "Cat A"));

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}