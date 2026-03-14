package com.perfumeria.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perfumeria.exception.CategoriaAlreadyExistsException;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.repositories.CategoriaProductoRepository;
import com.perfumeria.services.ICategoriaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService{
    
    @Autowired
    private CategoriaProductoRepository categoriaRepository;


    // CREAR CATEGORIA
    @Override
    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {

        categoriaRepository.findByNombre(categoria.getNombre()).ifPresent(c -> {
            throw new CategoriaAlreadyExistsException(categoria.getNombre());
        });

        return categoriaRepository.save(categoria);
    }


    // LISTAR CATEGORIAS
    @Override
    public List<CategoriaProducto> listarCategorias() {

        return categoriaRepository.findAll();
    }


    // BUSCAR POR ID
    @Override
    public CategoriaProducto buscarPorId(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }
   
    @Override
    public void eliminarCategoria(Long id) {

        CategoriaProducto categoria = buscarPorId(id);

        categoriaRepository.delete(categoria);
    }

    public CategoriaProducto actualizarCategoria(Long id, CategoriaProducto categoria) {
    CategoriaProducto existente = categoriaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    
    existente.setNombre(categoria.getNombre());
    return categoriaRepository.save(existente);
}

}