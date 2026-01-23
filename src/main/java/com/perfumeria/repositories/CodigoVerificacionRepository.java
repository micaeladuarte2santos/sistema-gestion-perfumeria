package com.perfumeria.repositories;

import com.perfumeria.models.CodigoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoVerificacionRepository extends JpaRepository<CodigoVerificacion, Long> {
    
    Optional<CodigoVerificacion> findByUsernameAndCodigoAndUsadoFalse(String username, String codigo);
    
    void deleteByUsername(String username);
}
