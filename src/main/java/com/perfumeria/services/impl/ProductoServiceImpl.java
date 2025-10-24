package com.perfumeria.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perfumeria.models.Producto;
import com.perfumeria.repositories.CategoriaProductoRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.services.IProductoService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements IProductoService{

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaProductoRepository categoriaRepository;

    @Override
    @Transactional
    public Producto crearProducto(Producto producto) {
        productoRepository.findByCodigoBarras(producto.getCodigoBarras()).ifPresent(p -> {throw new RuntimeException("Ya existe un producto con el mismo código de barras");});
        categoriaRepository.findById(producto.getCategoria().getId()).orElseThrow(() -> new RuntimeException("La categoría especificada no existe"));
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarrasAndActivoTrue(codigoBarras).orElseThrow(() -> new RuntimeException("No se encontró el producto con ese código de barras"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontró el producto con id" ));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

}
