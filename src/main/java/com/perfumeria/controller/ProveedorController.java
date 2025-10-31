package com.perfumeria.controller;

import com.perfumeria.models.Proveedor;
import com.perfumeria.services.IProveedorService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final IProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<Proveedor> agregarProveedor(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorService.agregarProveedor(proveedor));
    }

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
