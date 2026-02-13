package com.perfumeria.controller;

import com.perfumeria.dto.ReenviarCodigoRequestDTO;
import com.perfumeria.dto.UsuarioRequestDTO;
import com.perfumeria.dto.UsuarioResponseDTO;
import com.perfumeria.dto.VerificacionRequestDTO;
import com.perfumeria.dto.mapper.UsuarioMapper;
import com.perfumeria.exception.UsuarioNotFoundException;
import com.perfumeria.models.Usuario;
import com.perfumeria.services.IUsuarioService;
import com.perfumeria.repositories.UsuarioRepository; // IMPORTANTE
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORTANTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.perfumeria.dto.LoginRequestDTO;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private UsuarioRepository usuarioRepository; // AGREGADO: Para solucionar el error de compilación

    @Autowired
    private PasswordEncoder passwordEncoder; // AGREGADO: Para poder encriptar la nueva clave
    
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        boolean esValido = usuarioService.verificarCredenciales(request.getUsername(), request.getPassword());
        
        if (esValido) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Login exitoso");
            response.put("username", request.getUsername());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    // 1. Verificar existencia para resetPass1
    @GetMapping("/existe/{username}")
    public ResponseEntity<?> verificarExistencia(@PathVariable String username) {
        if (usuarioRepository.existsByUsername(username)) { // Usamos tu método del repo
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 2. Cambiar la clave para resetPass2
    @PostMapping("/actualizar-password")
    public ResponseEntity<?> actualizarPassword(@RequestBody Map<String, String> datos) {
        String username = datos.get("username");
        String nuevoPassword = datos.get("nuevoPassword");
        
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsuarioNotFoundException(username));
            
        usuario.setPassword(passwordEncoder.encode(nuevoPassword));
        usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente"));
    }
}