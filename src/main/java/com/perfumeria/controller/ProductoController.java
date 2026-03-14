package com.perfumeria.controller;

import com.perfumeria.dto.ProductoRequestDTO;
import com.perfumeria.dto.ProductoResponseDTO;
import com.perfumeria.dto.mapper.ProductoMapper;
import com.perfumeria.models.Producto;
import com.perfumeria.services.impl.ProductoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.nio.file.*;
import java.util.UUID;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@RestController
@RequestMapping("productos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoServiceImpl productoService;
    
    @Autowired
    private ProductoMapper productoMapper;

    @Value("${app.upload.dir}")
    private String uploadDir;


    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {

        Producto producto = productoService.obtenerPorId(id);

        return ResponseEntity.ok(productoMapper.toResponse(producto));
    }

    @PutMapping("/{id}/inactivar")
    public ResponseEntity<Void> inactivarProducto(@PathVariable Long id) {

        productoService.inactivarProducto(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @RequestPart("producto") ProductoRequestDTO request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        Producto producto = productoMapper.toEntity(request);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                String carpeta = "C:\\Users\\Abril\\OneDrive\\Pictures\\PPS";
                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();

                Path ruta = Paths.get(carpeta + nombreArchivo);
                Files.createDirectories(ruta.getParent());
                Files.copy(imagen.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

                producto.setImagen(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        Producto nuevo = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.toResponse(nuevo));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody ProductoRequestDTO request) {
        Producto producto = productoMapper.toEntity(request);
        Producto nuevo = productoService.crearProducto(producto);
        System.out.println("Categoria recibida: " + producto.getCategoria());
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
    public ResponseEntity<ProductoResponseDTO> actualizarPrecioYStock(@PathVariable Long id, @RequestParam(required = false) Double precio, @RequestParam(required = false) Double precioCosto, @RequestParam(required = false) Integer stock) {
        Producto actualizado = productoService.actualizarPrecioYStock(id, precio, precioCosto, stock);
        return ResponseEntity.ok(productoMapper.toResponse(actualizado));
    }

    @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ProductoResponseDTO> actualizarProducto(
        @PathVariable Long id,
        @RequestPart("producto") ProductoRequestDTO request,
        @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

    Producto producto = productoMapper.toEntity(request);
    producto.setId(id);

    if (imagen != null && !imagen.isEmpty()) {
        try {
            String carpeta = "C:\\Users\\Abril\\OneDrive\\Pictures\\PPS";
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();

            Path ruta = Paths.get(carpeta + nombreArchivo);
            Files.createDirectories(ruta.getParent());
            Files.copy(imagen.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING);

            producto.setImagen(nombreArchivo);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    Producto actualizado = productoService.actualizarProducto(producto);

    return ResponseEntity.ok(productoMapper.toResponse(actualizado));
}

}
