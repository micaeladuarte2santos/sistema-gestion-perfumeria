package com.perfumeria.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaRequestDTO {
    private String nombreCliente;
    private List<DetalleVentaRequestDTO> detalles;
}
