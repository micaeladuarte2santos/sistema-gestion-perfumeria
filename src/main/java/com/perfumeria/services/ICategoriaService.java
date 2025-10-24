package com.perfumeria.services;

import java.util.List;

import com.perfumeria.models.CategoriaProducto;

public interface ICategoriaService {
    CategoriaProducto crearCategoria(CategoriaProducto categoria);
    List<CategoriaProducto> listarCategorias();
}
