package com.perfumeria.services.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.perfumeria.exception.CategoriaNotFoundException;
import com.perfumeria.exception.ProductoCodigoBarrasAlreadyExistsException;
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
        productoRepository.findByCodigoBarras(producto.getCodigoBarras()).ifPresent(p -> {throw new ProductoCodigoBarrasAlreadyExistsException(producto.getCodigoBarras());});
        
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
        throw new IllegalArgumentException("La categoría es obligatoria");
}

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
    public Producto obtenerPorId(Long id) {
    return productoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public void inactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);

        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarrasAndActivoTrue(codigoBarras).orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con código de barras: " + codigoBarras));
    }

    @Transactional
    public Producto actualizarProducto(Producto producto) {

    Producto existente = productoRepository.findById(producto.getId())
        .orElseThrow(() -> new ProductoNotFoundException(producto.getId()));

    existente.setCodigoBarras(producto.getCodigoBarras());
    existente.setNombre(producto.getNombre());
    existente.setPrecio(producto.getPrecio());
    existente.setPrecioCosto(producto.getPrecioCosto());
    existente.setStock(producto.getStock());
    existente.setCategoria(producto.getCategoria());
    existente.setProveedor(producto.getProveedor());

    if (producto.getImagen() != null) {
        existente.setImagen(producto.getImagen());
    }

    return productoRepository.save(existente);
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

    
    @Override
    @Transactional
    public Producto actualizarPrecioYStock(Long id, Double precio, Double precioCosto, Integer stock) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
        if (precio != null) {
            producto.setPrecio(precio);
        }
        if (precioCosto != null) {
            producto.setPrecioCosto(precioCosto);
        }
        if (stock != null) {
            producto.setStock(stock);
        }
        return productoRepository.save(producto);
    }

}
