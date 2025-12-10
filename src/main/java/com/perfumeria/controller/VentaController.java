package com.perfumeria.controller;

import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.dto.VentaResponseDTO;
import com.perfumeria.dto.mapper.VentaMapper;
import com.perfumeria.models.Venta;
import com.perfumeria.services.impl.VentaServiceImpl;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    
    @Autowired
    private VentaMapper ventaMapper;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@RequestBody VentaRequestDTO request) {
        Venta venta = ventaMapper.toEntity(request);
        Venta nuevaVenta = ventaService.createVenta(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaMapper.toResponse(nuevaVenta));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> getAllVentas() {
        List<VentaResponseDTO> ventas = ventaService.findAll().stream()
            .map(ventaMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> getVentaById(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return ResponseEntity.ok(ventaMapper.toResponse(venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/mes")
    public ResponseEntity<List<VentaResponseDTO>> getVentasPorMes(@RequestParam int mes, @RequestParam int anio) {
        List<VentaResponseDTO> ventas = ventaService.findByMes(mes, anio).stream()
            .map(ventaMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/anio")
    public ResponseEntity<List<VentaResponseDTO>> getVentasPorAnio(@RequestParam int anio) {
        List<VentaResponseDTO> ventas = ventaService.findByAnio(anio).stream()
            .map(ventaMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/recaudacion/dia")
    public ResponseEntity<Double> getRecaudacionPorDia(@RequestParam String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return ResponseEntity.ok(ventaService.getRecaudacionPorDia(date));
    }

    @GetMapping("/recaudacion/mes")
    public ResponseEntity<Double> getRecaudacionPorMes(@RequestParam int mes, @RequestParam int anio) {
        return ResponseEntity.ok(ventaService.getRecaudacionPorMes(mes, anio));
    }

    @GetMapping("/recaudacion/anio")
    public ResponseEntity<Double> getRecaudacionPorAnio(@RequestParam int anio) {
        return ResponseEntity.ok(ventaService.getRecaudacionPorAnio(anio));
    }
}