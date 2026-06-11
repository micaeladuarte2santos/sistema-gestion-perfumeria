package com.perfumeria.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.UsuarioResponseDTO;
import com.perfumeria.models.Usuario;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void toEntity_mapsRequestToEntity() {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                "testuser",
                "password123",
                "Juan",
                "Pérez",
                "test@example.com",
                LocalDate.of(1990, 1, 1)
        );

        Usuario usuario = mapper.toEntity(request);

        assertNotNull(usuario);
        assertEquals("testuser", usuario.getUsername());
        assertEquals("password123", usuario.getPassword());
        assertEquals("Juan", usuario.getNombre());
        assertEquals("Pérez", usuario.getApellido());
        assertEquals("test@example.com", usuario.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), usuario.getFechaNacimiento());
    }

    @Test
    void toResponse_mapsEntityToResponse() {
        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("test@example.com");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));

        UsuarioResponseDTO response = mapper.toResponse(usuario);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("Juan", response.getNombre());
        assertEquals("Pérez", response.getApellido());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), response.getFechaNacimiento());
    }
}
