package com.perfumeria.dto.mapper;

import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.UsuarioResponseDTO;
import com.perfumeria.models.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toEntity(UsuarioRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        return usuario;
    }
    
    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getUsername());
    }
}
