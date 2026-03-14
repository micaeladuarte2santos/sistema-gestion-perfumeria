package com.perfumeria.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.dto.CategoriaRequestDTO;
import com.perfumeria.dto.CategoriaResponseDTO;
import com.perfumeria.dto.mapper.CategoriaMapper;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.services.impl.CategoriaServiceImpl;

@RestController
@RequestMapping("categorias")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CategoriaProductoController {

    @Autowired
    private CategoriaServiceImpl categoriaService;
    
    @Autowired
    private CategoriaMapper categoriaMapper;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(@RequestBody CategoriaRequestDTO request) {
        CategoriaProducto categoria = categoriaMapper.toEntity(request);
        CategoriaProducto nuevaCategoria = categoriaService.crearCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMapper.toResponse(nuevaCategoria));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarCategorias().stream()
            .map(categoriaMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProducto> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaProducto categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProducto> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaProducto categoria) {

        CategoriaProducto existente = categoriaService.buscarPorId(id);
        existente.setNombre(categoria.getNombre());

        CategoriaProducto actualizado = categoriaService.crearCategoria(existente); // O un método actualizar si querés
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
