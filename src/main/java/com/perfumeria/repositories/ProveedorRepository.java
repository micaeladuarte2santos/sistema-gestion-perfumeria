package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.perfumeria.models.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNombre(String nombre);
    boolean existsByEmailIgnoreCase(String email);
    List<Proveedor> findByActivoTrue();
}