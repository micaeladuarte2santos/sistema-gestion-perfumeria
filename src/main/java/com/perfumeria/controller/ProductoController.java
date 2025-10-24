package com.perfumeria.controller;

import com.perfumeria.models.Producto;
import com.perfumeria.services.impl.ProductoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoServiceImpl productoService;

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoService.crearProducto(producto);
        return ResponseEntity.ok(nuevo);
    }

    @GetMapping("listado-productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/buscar/{codigoBarras}")
    public ResponseEntity<Producto> obtenerPorCodigoBarras(@PathVariable String codigoBarras) {
        Producto producto = productoService.obtenerPorCodigoBarras(codigoBarras);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
    }
}
