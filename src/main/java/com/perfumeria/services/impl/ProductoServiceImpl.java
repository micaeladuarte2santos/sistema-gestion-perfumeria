package com.perfumeria.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.perfumeria.exception.CategoriaNotFoundException;
import com.perfumeria.exception.ProductoNotFoundException;
import com.perfumeria.exception.ProveedorNotFoundException;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Proveedor;
import com.perfumeria.repositories.CategoriaProductoRepository;
import com.perfumeria.repositories.ProductoRepository;
import com.perfumeria.repositories.ProveedorRepository;
import com.perfumeria.services.IProductoService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements IProductoService{

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaProductoRepository categoriaRepository;
    
    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public Producto crearProducto(Producto producto) {
        productoRepository.findByCodigoBarras(producto.getCodigoBarras()).ifPresent(p -> {throw new RuntimeException("Ya existe un producto con el mismo código de barras");});
        
        CategoriaProducto categoria = categoriaRepository.findById(producto.getCategoria().getId())
            .orElseThrow(() -> new CategoriaNotFoundException(producto.getCategoria().getId()));
        producto.setCategoria(categoria);
        
        if (producto.getProveedor() != null && producto.getProveedor().getId() != null) {
            Proveedor proveedor = proveedorRepository.findById(producto.getProveedor().getId())
                .orElseThrow(() -> new ProveedorNotFoundException(producto.getProveedor().getId()));
            producto.setProveedor(proveedor);
        }
        
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
        return productoRepository.findByCodigoBarrasAndActivoTrue(codigoBarras).orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con código de barras: " + codigoBarras));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

}
