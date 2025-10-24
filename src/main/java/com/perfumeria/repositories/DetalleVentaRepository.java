package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.perfumeria.models.DetalleVenta;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Venta;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    List<DetalleVenta> findByVenta(Venta venta);
    
    List<DetalleVenta> findByProducto(Producto producto);
    
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.venta.id = :ventaId")
    List<DetalleVenta> findByVentaId(@Param("ventaId") Long ventaId);
    
    @Query("SELECT dv FROM DetalleVenta dv WHERE dv.producto.id = :productoId")
    List<DetalleVenta> findByProductoId(@Param("productoId") Long productoId);
    
}