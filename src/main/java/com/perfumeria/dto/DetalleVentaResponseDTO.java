package com.perfumeria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaResponseDTO {
    private Long id;
    private ProductoResponseDTO producto;
    private int cantidad;
    private double subtotal;
}
