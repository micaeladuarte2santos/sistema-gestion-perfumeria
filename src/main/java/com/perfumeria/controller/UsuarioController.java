package com.perfumeria.controller;

import com.perfumeria.dto.ReenviarCodigoRequestDTO;
import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.UsuarioResponseDTO;
import com.perfumeria.dto.VerificacionRequestDTO;
import com.perfumeria.dto.mapper.UsuarioMapper;
import com.perfumeria.models.Usuario;
import com.perfumeria.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private UsuarioMapper usuarioMapper;
    
    @PostMapping
    public ResponseEntity<Map<String, String>> crearUsuario(@RequestBody UsuarioRequestDTO request) {
        Usuario usuario = usuarioMapper.toEntity(request);
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario creado exitosamente. Se ha enviado un código de verificación a tu correo electrónico.");
        response.put("username", nuevoUsuario.getUsername());
        response.put("email", nuevoUsuario.getEmail());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/verificar")
    public ResponseEntity<Map<String, String>> verificarUsuario(@RequestBody VerificacionRequestDTO request) {
        usuarioService.verificarUsuario(request.getUsername(), request.getCodigo());
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario verificado exitosamente. Ahora puedes iniciar sesión.");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Map<String, String>> reenviarCodigo(@RequestBody ReenviarCodigoRequestDTO request) {
        usuarioService.reenviarCodigoVerificacion(request.getUsername());
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Se ha reenviado un nuevo código de verificación a tu correo electrónico.");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String username) {
        usuarioService.eliminarUsuario(username);
        return ResponseEntity.noContent().build();
    }
}
