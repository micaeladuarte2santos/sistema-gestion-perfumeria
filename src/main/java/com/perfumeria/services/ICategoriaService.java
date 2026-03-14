package com.perfumeria.services;

import java.util.List;

import com.perfumeria.models.CategoriaProducto;

public interface ICategoriaService {
    
    CategoriaProducto crearCategoria(CategoriaProducto categoria);
    List<CategoriaProducto> listarCategorias();
    CategoriaProducto buscarPorId(Long id);
    CategoriaProducto actualizarCategoria(Long id, CategoriaProducto categoriaActualizada);
    void eliminarCategoria(Long id);
    
}
