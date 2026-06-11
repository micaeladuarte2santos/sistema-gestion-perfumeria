package com.perfumeria.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.perfumeria.models.Producto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void queries_basicas_funcionan() {
        Producto p1 = new Producto();
        p1.setCodigoBarras("111");
        p1.setNombre("Perfume A");
        p1.setActivo(true);

        Producto p2 = new Producto();
        p2.setCodigoBarras("222");
        p2.setNombre("Otro Producto");
        p2.setActivo(false);

        productoRepository.save(p1);
        productoRepository.save(p2);

        var byCodigo = productoRepository.findByCodigoBarrasAndActivoTrue("111");
        assertTrue(byCodigo.isPresent());

        List<Producto> activos = productoRepository.findByActivoTrue();
        assertTrue(activos.stream().anyMatch(prod -> "Perfume A".equals(prod.getNombre())));

        List<Producto> buscados = productoRepository.findByNombreContainingIgnoreCase("perfume");
        assertTrue(buscados.size() >= 1);
    }
}
