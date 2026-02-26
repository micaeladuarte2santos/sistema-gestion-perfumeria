package com.perfumeria.dto;

import com.perfumeria.models.MetodoPagoEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketVentaResponseDTO {
    private Long ventaId;
    private String nombreCliente;
    private LocalDateTime fecha;
    private MetodoPagoEnum metodoPago;
    private double total;
    private List<TicketDetalleResponseDTO> detalles;
}
