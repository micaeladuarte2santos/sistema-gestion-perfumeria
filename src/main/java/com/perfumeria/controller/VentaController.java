package com.perfumeria.controller;

import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.models.Venta;
import com.perfumeria.services.impl.VentaServiceImpl;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("ventas")
@RequiredArgsConstructor
public class VentaController {

    @Autowired
    private VentaServiceImpl ventaService;

    @PostMapping
    public Venta crearVenta(@RequestBody Venta venta) {
        return ventaService.createVenta(venta);
    }

    @GetMapping
    public ResponseEntity<List<Venta>> getAllVentas() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return ResponseEntity.ok(venta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/mes")
    public ResponseEntity<List<Venta>> getVentasPorMes(@RequestParam int mes, @RequestParam int anio) {
        return ResponseEntity.ok(ventaService.findByMes(mes, anio));
    }

    @GetMapping("/anio")
    public ResponseEntity<List<Venta>> getVentasPorAnio(@RequestParam int anio) {
        return ResponseEntity.ok(ventaService.findByAnio(anio));
    }
}