package com.perfumeria.dto.mapper;

import com.perfumeria.dto.ProveedorRequestDTO;
import com.perfumeria.dto.ProveedorResponseDTO;
import com.perfumeria.models.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    
    public Proveedor toEntity(ProveedorRequestDTO request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(request.getNombre());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        return proveedor;
    }
    
    public ProveedorResponseDTO toResponse(Proveedor proveedor) {
        return new ProveedorResponseDTO(
            proveedor.getId(),
            proveedor.getNombre(),
            proveedor.getTelefono(),
            proveedor.getEmail(),
            proveedor.getActivo()
        );
    }
}
