package com.perfumeria.controller;

import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.models.Venta;
import com.perfumeria.services.impl.VentaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
}