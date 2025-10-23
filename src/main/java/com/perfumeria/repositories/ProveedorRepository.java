package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfumeria.modeles.Proveedor;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNombre(String nombre);
}