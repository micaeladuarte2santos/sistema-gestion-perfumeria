package com.perfumeria.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.perfumeria.models.CodigoVerificacion;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CodigoVerificacionRepositoryTest {

    @Autowired
    private CodigoVerificacionRepository codigoVerificacionRepository;

    @Test
    void findByUsernameAndCodigoAndUsadoFalse_and_deleteByUsername() {
        CodigoVerificacion c = new CodigoVerificacion();
        c.setUsername("u1");
        c.setCodigo("123456");
        c.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        c.setUsado(false);

        codigoVerificacionRepository.save(c);

        var found = codigoVerificacionRepository.findByUsernameAndCodigoAndUsadoFalse("u1", "123456");
        assertTrue(found.isPresent());

        codigoVerificacionRepository.deleteByUsername("u1");
        var after = codigoVerificacionRepository.findByUsernameAndCodigoAndUsadoFalse("u1", "123456");
        assertTrue(after.isEmpty());
    }
}
