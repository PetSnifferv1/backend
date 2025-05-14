package br.com.petsniffer.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import br.com.petsniffer.app.config.TestDatabaseConfig;
import br.com.petsniffer.app.config.TestSecurityConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestDatabaseConfig.class})
class PetsnifferApplicationTests {

	@Test
	void contextLoads() {
	}

}
