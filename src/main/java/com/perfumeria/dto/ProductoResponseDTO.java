package com.perfumeria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoResponseDTO {
    private Long id;
    private String codigoBarras;
    private String nombre;
    private double precio;
    private int stock;
    private boolean activo;
    private String categoriaNombre;
    private String proveedorNombre;
}
