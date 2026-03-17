package com.perfumeria.controller;

import com.perfumeria.dto.ProveedorRequestDTO;
import com.perfumeria.dto.ProveedorResponseDTO;
import com.perfumeria.dto.mapper.ProveedorMapper;
import com.perfumeria.models.Proveedor;
import com.perfumeria.services.IProveedorService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final IProveedorService proveedorService;
    
    @Autowired
    private ProveedorMapper proveedorMapper;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> agregarProveedor(@RequestBody ProveedorRequestDTO request) {
        Proveedor proveedor = proveedorMapper.toEntity(request);
        Proveedor nuevoProveedor = proveedorService.agregarProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorMapper.toResponse(nuevoProveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(
        @PathVariable Long id,
        @RequestBody ProveedorRequestDTO request) {

    Proveedor proveedor = proveedorMapper.toEntity(request);

    Proveedor actualizado = proveedorService.actualizarProveedor(id, proveedor);

    return ResponseEntity.ok(proveedorMapper.toResponse(actualizado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedor(@PathVariable Long id){
    Proveedor proveedor = proveedorService.buscarPorId(id);
    return ResponseEntity.ok(proveedorMapper.toResponse(proveedor));
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> listarProveedores() {
        List<ProveedorResponseDTO> proveedores = proveedorService.listarProveedores().stream()
            .map(proveedorMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(proveedores);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
