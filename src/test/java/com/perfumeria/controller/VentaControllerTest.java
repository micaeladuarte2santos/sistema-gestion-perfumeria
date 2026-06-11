package com.perfumeria.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.perfumeria.dto.VentaResponseDTO;
import com.perfumeria.dto.mapper.VentaMapper;
import com.perfumeria.models.Venta;
import com.perfumeria.services.IVentaReporteService;
import com.perfumeria.services.IVentaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VentaController.class)
@AutoConfigureMockMvc(addFilters = false)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IVentaService ventaService;

    @MockBean
    private IVentaReporteService ventaReporteService;

    @MockBean
    private VentaMapper ventaMapper;

    @Test
    void getAllVentas_devuelveOk() throws Exception {
        Venta v = new Venta();
        v.setId(1L);
        v.setNombreCliente("C");

        when(ventaService.findAll()).thenReturn(List.of(v));
        when(ventaMapper.toResponse(v)).thenReturn(new VentaResponseDTO());

        mockMvc.perform(get("/ventas").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
