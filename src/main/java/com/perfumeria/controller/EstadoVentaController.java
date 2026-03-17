package com.perfumeria.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.models.EstadoVentaEnum;

@RestController
@RequestMapping("/estados-venta")
public class EstadoVentaController {

    @GetMapping
    public List<String> obtenerEstados() {
        return Arrays.stream(EstadoVentaEnum.values())
                     .map(Enum::name)
                     .toList();
    }
}