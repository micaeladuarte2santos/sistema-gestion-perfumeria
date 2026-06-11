package com.perfumeria;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SistemaGestionApplicationTests {

	@MockBean
	private com.perfumeria.services.IEmailService emailService;

	@Test
	void contextLoads() {
	}

}
