package com.perfumeria.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.repositories.CategoriaProductoRepository;
import com.perfumeria.services.ICategoriaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService{
    
    @Autowired
    private CategoriaProductoRepository categoriaRepository;

    @Override
    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    public List<CategoriaProducto> listarCategorias() {
        return categoriaRepository.findAll();
    }

}
