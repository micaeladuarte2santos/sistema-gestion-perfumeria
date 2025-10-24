package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfumeria.models.CategoriaProducto;
import com.perfumeria.models.Producto;
import com.perfumeria.models.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    
    Optional<Producto> findByCodigoBarrasAndActivoTrue(String codigoBarras);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    List<Producto> findByProveedor(Proveedor proveedor);
    List<Producto> findByProveedorAndActivoTrue(Proveedor proveedor);

    List<Producto> findByActivoTrue();


}