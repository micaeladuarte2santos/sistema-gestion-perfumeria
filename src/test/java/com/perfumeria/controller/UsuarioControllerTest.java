package com.perfumeria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfumeria.dto.LoginRequestDTO;
import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.mapper.UsuarioMapper;
import com.perfumeria.models.Usuario;
import com.perfumeria.services.IUsuarioService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUsuarioService usuarioService;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @Test
    void crearUsuario_devuelve201_yDatosDeUsuario() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "testuser",
                "password123",
                "Juan",
                "Pérez",
                "test@example.com",
                LocalDate.of(1990, 1, 1)
        );

        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setEmail("test@example.com");

        when(usuarioMapper.toEntity(any(UsuarioRequestDTO.class))).thenReturn(usuario);
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void login_devuelve200_cuandoCredencialesValidas() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(usuarioService.verificarCredenciales(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_devuelve401_cuandoCredencialesInvalidas() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername("testuser");
        request.setPassword("wrong-password");

        when(usuarioService.verificarCredenciales(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
