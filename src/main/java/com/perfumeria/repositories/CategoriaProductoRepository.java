package com.perfumeria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perfumeria.models.CategoriaProducto;

import java.util.Optional;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {
    Optional<CategoriaProducto> findByNombre(String nombre);
}