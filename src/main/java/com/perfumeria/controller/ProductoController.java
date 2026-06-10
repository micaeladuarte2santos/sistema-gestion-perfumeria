package com.perfumeria.controller;

import com.perfumeria.dto.ProductoRequestDTO;
import com.perfumeria.dto.ProductoResponseDTO;
import com.perfumeria.dto.mapper.ProductoMapper;
import com.perfumeria.models.Producto;
import com.perfumeria.services.IProductoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;
    private final ProductoMapper productoMapper;


    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {

        Producto producto = productoService.obtenerPorId(id);

        return ResponseEntity.ok(productoMapper.toResponse(producto));
    }


    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        try {

            List<ProductoResponseDTO> productos = productoService.listarProductos()
                    .stream()
                    .map(productoMapper::toResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(productos);

        } catch (Exception e) {
            e.printStackTrace(); 
            throw e; 
        }
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @RequestPart("producto") ProductoRequestDTO request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        Producto producto = productoMapper.toEntity(request);
        Producto nuevo = productoService.crearProducto(producto, imagen);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoMapper.toResponse(nuevo));
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @RequestPart("producto") ProductoRequestDTO request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        Producto producto = productoMapper.toEntity(request);
        producto.setId(id);
        Producto actualizado = productoService.actualizarProducto(producto, imagen);

        return ResponseEntity.ok(productoMapper.toResponse(actualizado));
    }


 
    @PutMapping("/{id}/stock-precio")
    public ResponseEntity<ProductoResponseDTO> actualizarPrecioYStock(
            @PathVariable Long id,
            @RequestParam(required = false) Double precio,
            @RequestParam(required = false) Double precioCosto,
            @RequestParam(required = false) Integer stock) {

        Producto actualizado = productoService.actualizarPrecioYStock(id, precio, precioCosto, stock);

        return ResponseEntity.ok(productoMapper.toResponse(actualizado));
    }


    
    @GetMapping("/buscar/{codigoBarras}")
    public ResponseEntity<ProductoResponseDTO> obtenerPorCodigoBarras(@PathVariable String codigoBarras) {

        Producto producto = productoService.obtenerPorCodigoBarras(codigoBarras);

        return ResponseEntity.ok(productoMapper.toResponse(producto));
    }



    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {

        List<ProductoResponseDTO> productos = productoService.listarPorCategoria(categoriaId)
                .stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productos);
    }


  
    @PutMapping("/{id}/inactivar")
    public ResponseEntity<Void> inactivarProducto(@PathVariable Long id) {

        productoService.inactivarProducto(id);

        return ResponseEntity.ok().build();
    }


   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {

        productoService.eliminarProducto(id);

        return ResponseEntity.noContent().build();
    }

}
