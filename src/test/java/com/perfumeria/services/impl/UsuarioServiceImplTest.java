package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.perfumeria.exception.CodigoVerificacionExpiradoException;
import com.perfumeria.exception.CodigoVerificacionInvalidoException;
import com.perfumeria.exception.UsuarioAlreadyExistsException;
import com.perfumeria.exception.UsuarioEmailAlreadyExistsException;
import com.perfumeria.exception.UsuarioNotFoundException;
import com.perfumeria.models.CodigoVerificacion;
import com.perfumeria.models.Usuario;
import com.perfumeria.repositories.CodigoVerificacionRepository;
import com.perfumeria.repositories.UsuarioRepository;
import com.perfumeria.services.IEmailService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CodigoVerificacionRepository codigoVerificacionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private CodigoVerificacion codigoVerificacion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setEmail("test@example.com");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setVerificado(false);

        codigoVerificacion = new CodigoVerificacion();
        codigoVerificacion.setUsername("testuser");
        codigoVerificacion.setCodigo("123456");
        codigoVerificacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        codigoVerificacion.setUsado(false);

        // Set the default value for minutosExpiracion
        ReflectionTestUtils.setField(usuarioService, "minutosExpiracion", 15);
    }

    @Test
    void testCrearUsuario_Exitoso() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(codigoVerificacionRepository.save(any(CodigoVerificacion.class))).thenReturn(codigoVerificacion);
        doNothing().when(emailService).enviarCodigoVerificacion(anyString(), anyString(), anyString());
        Usuario resultado = usuarioService.crearUsuario(usuario);
        assertNotNull(resultado);
        assertFalse(resultado.isVerificado());
        verify(usuarioRepository, times(1)).existsByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).existsByEmail(usuario.getEmail());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(codigoVerificacionRepository, times(1)).save(any(CodigoVerificacion.class));
        verify(emailService, times(1)).enviarCodigoVerificacion(anyString(), anyString(), anyString());
    }

    @Test
    void testCrearUsuario_LanzaExcepcionCuandoUsernameYaExiste() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(true);
        assertThrows(UsuarioAlreadyExistsException.class, () -> {
            usuarioService.crearUsuario(usuario);
        });

        verify(usuarioRepository, times(1)).existsByUsername(usuario.getUsername());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_LanzaExcepcionCuandoEmailYaExiste() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(UsuarioEmailAlreadyExistsException.class, () -> {
            usuarioService.crearUsuario(usuario);
        });

        verify(usuarioRepository, times(1)).existsByUsername(usuario.getUsername());
        verify(usuarioRepository, times(1)).existsByEmail(usuario.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_Exitoso() {
        when(usuarioRepository.existsById(anyString())).thenReturn(true);
        doNothing().when(codigoVerificacionRepository).deleteByUsername(anyString());
        doNothing().when(usuarioRepository).deleteById(anyString());
        assertDoesNotThrow(() -> {
            usuarioService.eliminarUsuario("testuser");
        });
        verify(usuarioRepository, times(1)).existsById("testuser");
        verify(codigoVerificacionRepository, times(1)).deleteByUsername("testuser");
        verify(usuarioRepository, times(1)).deleteById("testuser");
    }

    @Test
    void testEliminarUsuario_LanzaExcepcionCuandoNoExiste() {
        when(usuarioRepository.existsById(anyString())).thenReturn(false);
        assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.eliminarUsuario("noexiste");
        });

        verify(usuarioRepository, times(1)).existsById("noexiste");
        verify(usuarioRepository, never()).deleteById(anyString());
    }

    @Test
    void testVerificarUsuario_Exitoso() {
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        when(codigoVerificacionRepository.findByUsernameAndCodigoAndUsadoFalse(anyString(), anyString()))
            .thenReturn(Optional.of(codigoVerificacion));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(codigoVerificacionRepository.save(any(CodigoVerificacion.class))).thenReturn(codigoVerificacion);
        assertDoesNotThrow(() -> {
            usuarioService.verificarUsuario("testuser", "123456");
        });
        verify(usuarioRepository, times(1)).findById("testuser");
        verify(codigoVerificacionRepository, times(1)).findByUsernameAndCodigoAndUsadoFalse("testuser", "123456");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(codigoVerificacionRepository, times(1)).save(any(CodigoVerificacion.class));
    }

    @Test
    void testVerificarUsuario_LanzaExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.verificarUsuario("noexiste", "123456");
        });

        verify(usuarioRepository, times(1)).findById("noexiste");
        verify(codigoVerificacionRepository, never()).findByUsernameAndCodigoAndUsadoFalse(anyString(), anyString());
    }

    @Test
    void testVerificarUsuario_LanzaExcepcionCuandoCodigoInvalido() {
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        when(codigoVerificacionRepository.findByUsernameAndCodigoAndUsadoFalse(anyString(), anyString()))
            .thenReturn(Optional.empty());
        assertThrows(CodigoVerificacionInvalidoException.class, () -> {
            usuarioService.verificarUsuario("testuser", "999999");
        });

        verify(usuarioRepository, times(1)).findById("testuser");
        verify(codigoVerificacionRepository, times(1)).findByUsernameAndCodigoAndUsadoFalse("testuser", "999999");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testVerificarUsuario_LanzaExcepcionCuandoCodigoExpirado() {
        CodigoVerificacion codigoExpirado = new CodigoVerificacion();
        codigoExpirado.setUsername("testuser");
        codigoExpirado.setCodigo("123456");
        codigoExpirado.setFechaExpiracion(LocalDateTime.now().minusMinutes(1));
        codigoExpirado.setUsado(false);

        when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        when(codigoVerificacionRepository.findByUsernameAndCodigoAndUsadoFalse(anyString(), anyString()))
            .thenReturn(Optional.of(codigoExpirado));
        assertThrows(CodigoVerificacionExpiradoException.class, () -> {
            usuarioService.verificarUsuario("testuser", "123456");
        });

        verify(usuarioRepository, times(1)).findById("testuser");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testReenviarCodigoVerificacion_Exitoso() {
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        doNothing().when(codigoVerificacionRepository).deleteByUsername(anyString());
        when(codigoVerificacionRepository.save(any(CodigoVerificacion.class))).thenReturn(codigoVerificacion);
        doNothing().when(emailService).enviarCodigoVerificacion(anyString(), anyString(), anyString());
        assertDoesNotThrow(() -> {
            usuarioService.reenviarCodigoVerificacion("testuser");
        });
        verify(usuarioRepository, times(1)).findById("testuser");
        verify(codigoVerificacionRepository, times(1)).deleteByUsername("testuser");
        verify(codigoVerificacionRepository, times(1)).save(any(CodigoVerificacion.class));
        verify(emailService, times(1)).enviarCodigoVerificacion(anyString(), anyString(), anyString());
    }

    @Test
    void testReenviarCodigoVerificacion_LanzaExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(UsuarioNotFoundException.class, () -> {
            usuarioService.reenviarCodigoVerificacion("noexiste");
        });

        verify(usuarioRepository, times(1)).findById("noexiste");
        verify(codigoVerificacionRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void testReenviarCodigoVerificacion_LanzaExcepcionCuandoUsuarioYaVerificado() {
        usuario.setVerificado(true);
        when(usuarioRepository.findById(anyString())).thenReturn(Optional.of(usuario));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.reenviarCodigoVerificacion("testuser");
        });

        assertTrue(exception.getMessage().contains("ya está verificado"));
        verify(usuarioRepository, times(1)).findById("testuser");
        verify(codigoVerificacionRepository, never()).deleteByUsername(anyString());
    }
}
