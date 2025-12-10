package com.perfumeria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoRequestDTO {
    private String codigoBarras;
    private String nombre;
    private double precio;
    private int stock;
    private Long categoriaId;
    private Long proveedorId;
}
