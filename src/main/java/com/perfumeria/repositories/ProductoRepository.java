package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfumeria.modeles.CategoriaProducto;
import com.perfumeria.modeles.Producto;
import com.perfumeria.modeles.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    List<Producto> findByProveedor(Proveedor proveedor);

}