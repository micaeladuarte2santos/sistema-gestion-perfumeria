package com.perfumeria.controller;

import com.perfumeria.models.MetodoPagoEnum;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/metodos-pago")
public class MetodoPagoController {

    @GetMapping
    public List<MetodoPagoEnum> listar() {
        return Arrays.asList(MetodoPagoEnum.values());
    }
} 
    

