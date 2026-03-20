package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.perfumeria.models.EstadoVentaEnum;
import com.perfumeria.models.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByNombreClienteContainingIgnoreCase(String nombreCliente);
    
    List<Venta> findByOrderByFechaDesc();

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT v FROM Venta v WHERE FUNCTION('DATE', v.fecha) = FUNCTION('DATE', :fecha)")
    List<Venta> findByFecha(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT v FROM Venta v WHERE MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :anio")
    List<Venta> findByMes(@Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT v FROM Venta v WHERE YEAR(v.fecha) = :anio")
    List<Venta> findByAnio(@Param("anio") int anio);

    @Query("""
SELECT v FROM Venta v
LEFT JOIN FETCH v.detalles d
LEFT JOIN FETCH d.producto
WHERE v.id = :id
""")
    Optional<Venta> findByIdConDetalles(@Param("id") Long id);

    // 💰 RECAUDACIÓN
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = 'ABONADA'")
    Double getRecaudacionPorDia(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    @Query("SELECT SUM(v.total) FROM Venta v WHERE MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :anio AND v.estado = 'ABONADA'")
    Double getRecaudacionPorMes(@Param("mes") int mes, @Param("anio") int anio);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE YEAR(v.fecha) = :anio AND v.estado = 'ABONADA'")
    Double getRecaudacionPorAnio(@Param("anio") int anio);

    // 🔥 DEVOLUCIONES DIA
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
    Long countDevolucionesDia(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estado") EstadoVentaEnum estado
    );

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
    Double totalDevolucionesDia(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("estado") EstadoVentaEnum estado
    );

    // 🔥 MES
@Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
Long countDevolucionesMes(
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin,
    @Param("estado") EstadoVentaEnum estado
);

@Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
Double totalDevolucionesMes(
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin,
    @Param("estado") EstadoVentaEnum estado
);

// 🔥 AÑO
@Query("SELECT COUNT(v) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
Long countDevolucionesAnio(
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin,
    @Param("estado") EstadoVentaEnum estado
);

@Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.estado = :estado")
Double totalDevolucionesAnio(
    @Param("inicio") LocalDateTime inicio,
    @Param("fin") LocalDateTime fin,
    @Param("estado") EstadoVentaEnum estado
);
}