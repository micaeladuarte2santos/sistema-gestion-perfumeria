package com.perfumeria.dto;

import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.MetodoPagoEnum;
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
    private EstadoVentaEnum estado;
    private MetodoPagoEnum metodoPago;
}
