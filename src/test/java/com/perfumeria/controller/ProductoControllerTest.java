package com.perfumeria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.perfumeria.dto.ProductoResponseDTO;
import com.perfumeria.dto.mapper.ProductoMapper;
import com.perfumeria.models.Producto;
import com.perfumeria.services.IProductoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductoService productoService;

    @MockBean
    private ProductoMapper productoMapper;

    @Test
    void listarProductos_devuelveLista() throws Exception {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Perfume A");

        when(productoService.listarProductos()).thenReturn(List.of(p));
        when(productoMapper.toResponse(any(Producto.class))).thenReturn(new ProductoResponseDTO(1L,"111","Perfume A",100.0,80.0,5,true,null,null,null,null,null));

        mockMvc.perform(get("/productos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].nombre").value("Perfume A"));
    }
}
