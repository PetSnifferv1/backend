package br.com.petsniffer.app.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.petsniffer.app.config.TestDatabaseConfig;
import br.com.petsniffer.app.config.TestSecurityConfig;
import br.com.petsniffer.app.domain.user.UserRole;
import br.com.petsniffer.app.domain.user.dtos.AuthenticationDTO;
import br.com.petsniffer.app.domain.user.dtos.RegisterDTO;
import br.com.petsniffer.app.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestDatabaseConfig.class})
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void whenRegisterAndLogin_thenSuccess() throws Exception {
        // Register
        RegisterDTO registerDTO = new RegisterDTO("testuser", "password", "test@email.com", UserRole.USER);
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk());

        // Login
        AuthenticationDTO loginDTO = new AuthenticationDTO("testuser", "password");
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk());
    }
} 