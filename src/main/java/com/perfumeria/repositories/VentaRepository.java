package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.perfumeria.modeles.Venta;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByNombreClienteContainingIgnoreCase(String nombreCliente);
    
    List<Venta> findByOrderByFechaDesc();
    
    @Query("SELECT v FROM Venta v WHERE DATE(v.fecha) = DATE(:fecha)")
    List<Venta> findByFecha(@Param("fecha") LocalDateTime fecha);
    
}