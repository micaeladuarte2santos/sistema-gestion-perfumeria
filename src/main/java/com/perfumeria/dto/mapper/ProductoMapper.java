package com.perfumeria.dto.mapper;

import com.perfumeria.dto.ProductoRequestDTO;
import com.perfumeria.dto.ProductoResponseDTO;
import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    
    public Producto toEntity(ProductoRequestDTO request) {
        Producto producto = new Producto();
        producto.setCodigoBarras(request.getCodigoBarras());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setPrecioCosto(request.getPrecioCosto());
        producto.setStock(request.getStock());
        
        if (request.getCategoriaId() != null) {
            CategoriaProducto categoria = new CategoriaProducto();
            categoria.setId(request.getCategoriaId());
            producto.setCategoria(categoria);
        }
        
        if (request.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(request.getProveedorId());
            producto.setProveedor(proveedor);
        }
        
        return producto;
    }
    
    public ProductoResponseDTO toResponse(Producto producto) {
        ProductoResponseDTO response = new ProductoResponseDTO();
        response.setId(producto.getId());
        response.setCodigoBarras(producto.getCodigoBarras());
        response.setNombre(producto.getNombre());
        response.setPrecio(producto.getPrecio());
        response.setPrecioCosto(producto.getPrecioCosto());
        response.setStock(producto.getStock());
        response.setActivo(producto.isActivo());
        
        if (producto.getCategoria() != null) {
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        
        if (producto.getProveedor() != null) {
            response.setProveedorNombre(producto.getProveedor().getNombre());
        }
        
        return response;
    }
}
