package com.perfumeria.services.impl;

import com.perfumeria.exception.UsuarioAlreadyExistsException;
import com.perfumeria.exception.UsuarioNotFoundException;
import com.perfumeria.exception.UsuarioEmailAlreadyExistsException;
import com.perfumeria.exception.CodigoVerificacionInvalidoException;
import com.perfumeria.exception.CodigoVerificacionExpiradoException;
import com.perfumeria.models.CodigoVerificacion;
import com.perfumeria.models.Usuario;
import com.perfumeria.repositories.CodigoVerificacionRepository;
import com.perfumeria.repositories.UsuarioRepository;
import com.perfumeria.services.IEmailService;
import com.perfumeria.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final CodigoVerificacionRepository codigoVerificacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    
    @Value("${verificacion.codigo.expiracion.minutos:15}")
    private int minutosExpiracion;

    @Override
    public boolean verificarCredenciales(String username, String password) {
        // Buscamos al usuario por su username
        return usuarioRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword())) 
                .orElse(false); 
    }

    @Override
    public boolean existeUsuario(String username) {
        return usuarioRepository.existsByUsername(username);
    }
    
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsuarioAlreadyExistsException(usuario.getUsername());
        }
        if (usuario.getEmail() != null && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new UsuarioEmailAlreadyExistsException(usuario.getEmail());
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setVerificado(false);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        generarYEnviarCodigo(usuarioGuardado);
        
        return usuarioGuardado;
    }

    @Override
    public void eliminarUsuario(String username) {
        if (!usuarioRepository.existsById(username)) {
            throw new UsuarioNotFoundException(username);
        }
        codigoVerificacionRepository.deleteByUsername(username);
        usuarioRepository.deleteById(username);
    }
    
    @Override
    public void verificarUsuario(String username, String codigo) {
        Usuario usuario = usuarioRepository.findById(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));
        
        CodigoVerificacion codigoVerificacion = codigoVerificacionRepository
            .findByUsernameAndCodigoAndUsadoFalse(username, codigo)
            .orElseThrow(() -> new CodigoVerificacionInvalidoException());
        
        if (codigoVerificacion.estaExpirado()) {
            throw new CodigoVerificacionExpiradoException();
        }
        
        usuario.setVerificado(true);
        usuarioRepository.save(usuario);
        
        codigoVerificacion.setUsado(true);
        codigoVerificacionRepository.save(codigoVerificacion);
    }
    
    @Override
    public void reenviarCodigoVerificacion(String username) {
        Usuario usuario = usuarioRepository.findById(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));
        
        if (usuario.isVerificado()) {
            throw new RuntimeException("El usuario ya está verificado");
        }
        
        codigoVerificacionRepository.deleteByUsername(username);
        generarYEnviarCodigo(usuario);
    }
    
    private void generarYEnviarCodigo(Usuario usuario) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        
        CodigoVerificacion codigoVerificacion = new CodigoVerificacion();
        codigoVerificacion.setUsername(usuario.getUsername());
        codigoVerificacion.setCodigo(codigo);
        codigoVerificacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion));
        codigoVerificacion.setUsado(false);
        codigoVerificacionRepository.save(codigoVerificacion);

        try {
            emailService.enviarCodigoVerificacion(
                usuario.getEmail(), 
                codigo, 
                usuario.getNombre() + " " + usuario.getApellido()
            );
        } catch (Exception e) {
            // Imprimimos el error en consola para monitoreo
            System.err.println("ERROR: No se pudo enviar el correo de verificación: " + e.getMessage());
        }
    }
       
    @Override
    @Transactional 
    public void actualizarPassword(String username, String nuevoPassword) {
        // Buscamos al usuario por su username
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));
        
        // Seteamos la nueva clave encriptada
        usuario.setPassword(passwordEncoder.encode(nuevoPassword));
        
        // Guardamos los cambios
        usuarioRepository.save(usuario);
    }

    @Override
    public void solicitarCodigoRecuperacion(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));

        codigoVerificacionRepository.deleteByUsername(username);

        String codigo = String.format("%06d", new Random().nextInt(1_000_000));
        CodigoVerificacion codigoVerificacion = new CodigoVerificacion();
        codigoVerificacion.setUsername(username);
        codigoVerificacion.setCodigo(codigo);
        codigoVerificacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(minutosExpiracion));
        codigoVerificacion.setUsado(false);
        codigoVerificacionRepository.save(codigoVerificacion);

        emailService.enviarCodigoRecuperacion(
            usuario.getEmail(),
            codigo,
            usuario.getNombre() + " " + usuario.getApellido()
        );
    }

    @Override
    public void actualizarPasswordConCodigo(String username, String codigo, String nuevoPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));

        CodigoVerificacion codigoVerificacion = codigoVerificacionRepository
            .findByUsernameAndCodigoAndUsadoFalse(username, codigo)
            .orElseThrow(CodigoVerificacionInvalidoException::new);

        if (codigoVerificacion.estaExpirado()) {
            throw new CodigoVerificacionExpiradoException();
        }

        usuario.setPassword(passwordEncoder.encode(nuevoPassword));
        usuarioRepository.save(usuario);

        codigoVerificacion.setUsado(true);
        codigoVerificacionRepository.save(codigoVerificacion);
    }
}
