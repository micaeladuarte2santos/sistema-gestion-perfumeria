package com.perfumeria.controller;

import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.UsuarioResponseDTO;
import com.perfumeria.dto.mapper.UsuarioMapper;
import com.perfumeria.models.Usuario;
import com.perfumeria.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioRequestDTO request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toResponse(nuevoUsuario));
    }
    
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String username) {
        usuarioService.eliminarUsuario(username);
        return ResponseEntity.noContent().build();
    }
}
