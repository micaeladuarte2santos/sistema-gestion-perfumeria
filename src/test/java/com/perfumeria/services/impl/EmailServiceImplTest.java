package com.perfumeria.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImpl emailService;

    private String destinatario;
    private String codigo;
    private String nombreUsuario;

    @BeforeEach
    void setUp() {
        destinatario = "test@example.com";
        codigo = "123456";
        nombreUsuario = "Juan Perez";
    }

    @Test
    void testEnviarCodigoVerificacion_Exitoso() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        assertDoesNotThrow(() -> {
            emailService.enviarCodigoVerificacion(destinatario, codigo, nombreUsuario);
        });
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarCodigoVerificacion_LanzaRuntimeExceptionCuandoFalla() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Error de conexión")).when(mailSender).send(any(MimeMessage.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.enviarCodigoVerificacion(destinatario, codigo, nombreUsuario);
        });

        assertTrue(exception.getMessage().contains("Error"));
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testEnviarCodigoVerificacion_ConDiferentesParametros() {
        String otroDestinatario = "otro@example.com";
        String otroCodigo = "654321";
        String otroNombre = "Maria Garcia";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        assertDoesNotThrow(() -> {
            emailService.enviarCodigoVerificacion(otroDestinatario, otroCodigo, otroNombre);
        });
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
