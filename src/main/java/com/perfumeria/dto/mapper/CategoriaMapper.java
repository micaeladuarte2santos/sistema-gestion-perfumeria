package com.perfumeria.dto.mapper;

import com.perfumeria.dto.CategoriaRequestDTO;
import com.perfumeria.dto.CategoriaResponseDTO;
import com.perfumeria.models.CategoriaProducto;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    
    public CategoriaProducto toEntity(CategoriaRequestDTO request) {
        CategoriaProducto categoria = new CategoriaProducto();
        categoria.setNombre(request.getNombre());
        return categoria;
    }
    
    public CategoriaResponseDTO toResponse(CategoriaProducto categoria) {
        return new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNombre()
        );
    }
}
