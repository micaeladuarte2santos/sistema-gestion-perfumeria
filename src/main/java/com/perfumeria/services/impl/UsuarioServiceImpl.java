package com.perfumeria.services.impl;

import com.perfumeria.exception.UsuarioAlreadyExistsException;
import com.perfumeria.exception.UsuarioNotFoundException;
import com.perfumeria.models.Usuario;
import com.perfumeria.repositories.UsuarioRepository;
import com.perfumeria.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new UsuarioAlreadyExistsException(usuario.getUsername());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(String username) {
        if (!usuarioRepository.existsById(username)) {
            throw new UsuarioNotFoundException(username);
        }
        usuarioRepository.deleteById(username);
    }
    
}
