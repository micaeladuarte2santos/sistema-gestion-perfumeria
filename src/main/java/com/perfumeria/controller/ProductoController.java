package com.perfumeria.controller;

import com.perfumeria.dto.ProductoRequestDTO;
import com.perfumeria.dto.ProductoResponseDTO;
import com.perfumeria.dto.mapper.ProductoMapper;
import com.perfumeria.models.Producto;
import com.perfumeria.services.impl.ProductoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoServiceImpl productoService;
    
    @Autowired
    private ProductoMapper productoMapper;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody ProductoRequestDTO request) {
        Producto producto = productoMapper.toEntity(request);
        Producto nuevo = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.toResponse(nuevo));
    }

    @GetMapping("listado-productos")
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        List<ProductoResponseDTO> productos = productoService.listarProductos().stream()
            .map(productoMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar/{codigoBarras}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorCodigoBarras(@PathVariable String codigoBarras) {
        Producto producto = productoService.obtenerPorCodigoBarras(codigoBarras);
        return ResponseEntity.ok(productoMapper.toResponse(producto));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<ProductoResponseDTO> productos = productoService.listarPorCategoria(categoriaId).stream()
            .map(productoMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarPrecioYStock(@PathVariable Long id, @RequestParam(required = false) Double precio, @RequestParam(required = false) Integer stock) {
        Producto actualizado = productoService.actualizarPrecioYStock(id, precio, stock);
        return ResponseEntity.ok(productoMapper.toResponse(actualizado));
    }

}
