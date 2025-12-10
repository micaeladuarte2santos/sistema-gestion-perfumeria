package com.perfumeria.dto;

import com.perfumeria.models.EstadoVenta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaResponseDTO {
    private Long id;
    private String nombreCliente;
    private LocalDateTime fecha;
    private List<DetalleVentaResponseDTO> detalles;
    private double total;
    private EstadoVenta estado;
}
