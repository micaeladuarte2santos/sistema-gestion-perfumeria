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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CodigoVerificacionRepository codigoVerificacionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private IEmailService emailService;
    
    @Value("${verificacion.codigo.expiracion.minutos:15}")
    private int minutosExpiracion;
    
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

        emailService.enviarCodigoVerificacion(
            usuario.getEmail(), 
            codigo, 
            usuario.getNombre() + " " + usuario.getApellido()
        );
    }
}
