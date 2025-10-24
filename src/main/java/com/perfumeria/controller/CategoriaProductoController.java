package com.perfumeria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.services.impl.CategoriaServiceImpl;

@RestController
@RequestMapping("categorias")
public class CategoriaProductoController {

    @Autowired
    private CategoriaServiceImpl categoriaService;

    @PostMapping
    public CategoriaProducto crearCategoria(@RequestBody CategoriaProducto categoria) {
        return categoriaService.crearCategoria(categoria);
    }

    @GetMapping
    public List<CategoriaProducto> listarCategorias() {
        return categoriaService.listarCategorias();
    }

}
