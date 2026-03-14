package com.perfumeria.dto.mapper;

import com.perfumeria.dto.TicketDetalleResponseDTO;
import com.perfumeria.dto.TicketVentaResponseDTO;
import com.perfumeria.dto.VentaRequestDTO;
import com.perfumeria.dto.VentaResponseDTO;
import com.perfumeria.models.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class VentaMapper {

    private static final DateTimeFormatter TICKET_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
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

    public TicketVentaResponseDTO toTicketResponse(Venta venta) {
        TicketVentaResponseDTO response = new TicketVentaResponseDTO();
        response.setVentaId(venta.getId());
        response.setNombreCliente(venta.getNombreCliente());
        response.setFecha(venta.getFecha());
        response.setMetodoPago(venta.getMetodoPago());
        response.setTotal(venta.getTotal());

        if (venta.getDetalles() != null) {
            response.setDetalles(
                venta.getDetalles().stream()
                    .map(detalle -> new TicketDetalleResponseDTO(
                        detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto",
                        detalle.getCantidad(),
                        detalle.getProducto() != null ? detalle.getProducto().getPrecio() : 0.0,
                        detalle.getSubtotal()
                    ))
                    .collect(Collectors.toList())
            );
        } else {
            response.setDetalles(new ArrayList<>());
        }

        return response;
    }

    public String toTicketText(Venta venta) {
        TicketVentaResponseDTO ticket = toTicketResponse(venta);

        StringBuilder sb = new StringBuilder();
        sb.append("******** PERFUMERIA ********\n");
        sb.append("Ticket N°: ").append(ticket.getVentaId()).append("\n");
        sb.append("Fecha: ").append(ticket.getFecha() != null ? ticket.getFecha().format(TICKET_DATE_FORMATTER) : "-").append("\n");
        sb.append("Cliente: ").append(ticket.getNombreCliente() != null ? ticket.getNombreCliente() : "Consumidor final").append("\n");
        sb.append("Metodo pago: ").append(ticket.getMetodoPago() != null ? ticket.getMetodoPago().name() : "-").append("\n");
        sb.append("--------------------------------\n");

        for (TicketDetalleResponseDTO detalle : ticket.getDetalles()) {
            sb.append(detalle.getProducto()).append("\n");
            sb.append(String.format("%d x %.2f = %.2f", detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.getSubtotal())).append("\n");
        }

        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL: %.2f", ticket.getTotal())).append("\n");
        sb.append("********************************\n");

        return sb.toString();
    }
}
