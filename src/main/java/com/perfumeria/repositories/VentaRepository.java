package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.perfumeria.models.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByNombreClienteContainingIgnoreCase(String nombreCliente);
    
    List<Venta> findByOrderByFechaDesc();
    
    @Query("SELECT v FROM Venta v WHERE DATE(v.fecha) = DATE(:fecha)")
    List<Venta> findByFecha(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT v FROM Venta v WHERE MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :anio")
    List<Venta> findByMes(@Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT v FROM Venta v WHERE YEAR(v.fecha) = :anio")
    List<Venta> findByAnio(@Param("anio") int anio);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fecha) = :fecha")
    Double getRecaudacionPorDia(@Param("fecha") LocalDate fecha);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :anio")
    Double getRecaudacionPorMes(@Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE YEAR(v.fecha) = :anio")
    Double getRecaudacionPorAnio(@Param("anio") int anio);
    
}