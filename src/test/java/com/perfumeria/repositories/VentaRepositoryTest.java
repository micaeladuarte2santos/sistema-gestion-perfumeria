package com.perfumeria.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.MetodoPagoEnum;
import com.perfumeria.models.Venta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class VentaRepositoryTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Test
    void customQueries_debenRetornarInfoEsperada() {
        Venta ventaAbonada = new Venta();
        ventaAbonada.setNombreCliente("Cliente A");
        ventaAbonada.setFecha(LocalDateTime.of(2026, 6, 10, 10, 0));
        ventaAbonada.setTotal(100.0);
        ventaAbonada.setEstado(EstadoVentaEnum.ABONADA);
        ventaAbonada.setMetodoPago(MetodoPagoEnum.EFECTIVO);

        Venta ventaCancelada = new Venta();
        ventaCancelada.setNombreCliente("Cliente B");
        ventaCancelada.setFecha(LocalDateTime.of(2026, 6, 10, 12, 0));
        ventaCancelada.setTotal(42.0);
        ventaCancelada.setEstado(EstadoVentaEnum.CANCELADA);
        ventaCancelada.setMetodoPago(MetodoPagoEnum.DEBITO);

        ventaRepository.save(ventaAbonada);
        ventaRepository.save(ventaCancelada);

        LocalDateTime inicio = LocalDateTime.of(2026, 6, 10, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 11, 0, 0);

        List<Venta> ventasMes = ventaRepository.findByMes(6, 2026);
        assertTrue(ventasMes.stream().anyMatch(v -> "Cliente A".equals(v.getNombreCliente())));
        assertTrue(ventasMes.stream().anyMatch(v -> "Cliente B".equals(v.getNombreCliente())));

        Double recaudacionDia = ventaRepository.getRecaudacionPorDia(inicio, fin);
        assertEquals(100.0, recaudacionDia);

        Long devoluciones = ventaRepository.countDevolucionesDia(inicio, fin, EstadoVentaEnum.CANCELADA);
        assertEquals(1L, devoluciones);

        Optional<Venta> ventaConDetalles = ventaRepository.findByIdConDetalles(ventaAbonada.getId());
        assertTrue(ventaConDetalles.isPresent());
        assertEquals(ventaAbonada.getNombreCliente(), ventaConDetalles.get().getNombreCliente());
    }
}
