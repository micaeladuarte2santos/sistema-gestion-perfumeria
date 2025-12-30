package com.perfumeria.dto.mapper;

import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.dto.VentaResponseDTO;
import com.perfumeria.models.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class VentaMapper {
    
    @Autowired
    private DetalleVentaMapper detalleVentaMapper;
    
    public Venta toEntity(VentaRequestDTO request) {
        Venta venta = new Venta();
        venta.setNombreCliente(request.getNombreCliente());
        venta.setMetodoPago(request.getMetodoPago());
        
        if (request.getDetalles() != null) {
            venta.setDetalles(
                request.getDetalles().stream()
                    .map(detalleVentaMapper::toEntity)
                    .collect(Collectors.toList())
            );
        }
        
        return venta;
    }
    
    public VentaResponseDTO toResponse(Venta venta) {
        VentaResponseDTO response = new VentaResponseDTO();
        response.setId(venta.getId());
        response.setNombreCliente(venta.getNombreCliente());
        response.setFecha(venta.getFecha());
        response.setTotal(venta.getTotal());
        response.setEstado(venta.getEstado());
        response.setMetodoPago(venta.getMetodoPago());
        
        if (venta.getDetalles() != null) {
            response.setDetalles(
                venta.getDetalles().stream()
                    .map(detalleVentaMapper::toResponse)
                    .collect(Collectors.toList())
            );
        }
        
        return response;
    }
}
