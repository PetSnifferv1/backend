package br.com.petsniffer.app.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import br.com.petsniffer.app.controller.AuthenticationController;
import br.com.petsniffer.app.domain.user.User;
import br.com.petsniffer.app.domain.user.UserRole;
import br.com.petsniffer.app.domain.user.dtos.RegisterDTO;
import br.com.petsniffer.app.infra.security.TokenService;
import br.com.petsniffer.app.repositories.UserRepository;

class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenRegisterWithValidData_thenSuccess() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("testuser", "password", "test@email.com", UserRole.USER);
        when(userRepository.findByLogin(any())).thenReturn(null);

        // Act
        ResponseEntity response = authenticationController.register(registerDTO);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenRegisterWithExistingLogin_thenBadRequest() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("existinguser", "password", "test@email.com", UserRole.USER);
        when(userRepository.findByLogin(any())).thenReturn(new User());

        // Act
        ResponseEntity response = authenticationController.register(registerDTO);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        verify(userRepository, never()).save(any(User.class));
    }
} 