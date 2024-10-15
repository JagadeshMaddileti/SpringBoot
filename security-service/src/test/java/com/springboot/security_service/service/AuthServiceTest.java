package com.springboot.security_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.springboot.security_service.model.UserCredentials;
import com.springboot.security_service.repository.UserCredentialsRepository;
import com.springboot.security_service.util.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    @Mock
    private UserCredentialsRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_ShouldEncodePasswordAndSaveUser() {
        UserCredentials credentials = new UserCredentials(1,"user", "user@example.com", "password");
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        String result = authService.saveUser(credentials);

        assertEquals("User added to the System", result);
        assertEquals("encodedPassword", credentials.getPassword());
        verify(repository, times(1)).save(credentials);
    }

    @Test
    void generateToken_ShouldReturnToken() {
        String username = "user";
        String expectedToken = "token";
        when(jwtService.generateToken(username)).thenReturn(expectedToken);

        String result = authService.generateToken(username);

        assertEquals(expectedToken, result);
        verify(jwtService, times(1)).generateToken(username);
    }

    @Test
    void validateToken_ShouldCallValidateTokenOnJwtService() {
        String token = "validToken";
        doNothing().when(jwtService).validateToken(token);

        authService.validateToken(token);

        verify(jwtService, times(1)).validateToken(token);
    }
}
