package com.perfumeria.dto.mapper;

import com.perfumeria.dto.DetalleVentaRequestDTO;
import com.perfumeria.dto.DetalleVentaResponseDTO;
import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalleVentaMapper {
    
    @Autowired
    private ProductoMapper productoMapper;
    
    public DetalleVenta toEntity(DetalleVentaRequestDTO request) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(request.getCantidad());
        
        if (request.getProductoId() != null) {
            Producto producto = new Producto();
            producto.setId(request.getProductoId());
            detalle.setProducto(producto);
        }
        
        return detalle;
    }
    
    public DetalleVentaResponseDTO toResponse(DetalleVenta detalle) {
        DetalleVentaResponseDTO response = new DetalleVentaResponseDTO();
        response.setId(detalle.getId());
        response.setCantidad(detalle.getCantidad());
        response.setSubtotal(detalle.getSubtotal());
        
        if (detalle.getProducto() != null) {
            response.setProducto(productoMapper.toResponse(detalle.getProducto()));
        }
        
        return response;
    }
}
