package com.springboot.security_service.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.springboot.security_service.dto.AuthRequest;
import com.springboot.security_service.exception.InvalidCredentialsException;
import com.springboot.security_service.model.UserCredentials;
import com.springboot.security_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Stream;

import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewUser_Success() throws InvalidCredentialsException {
        UserCredentials user = new UserCredentials();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("Test1234");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(authService.saveUser(user)).thenReturn("User saved successfully");

        String response = authController.addNewUser(user, bindingResult);

        assertEquals("User saved successfully", response);
        verify(authService).saveUser(user);
    }

    @Test
    void testAddNewUser_ValidationError() {
        UserCredentials user = new UserCredentials();
        user.setName("");  // Invalid name (blank)
        user.setEmail("test@example.com");
        user.setPassword("Test1234");

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("user", "name", "Name is required")
        ));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                authController.addNewUser(user, bindingResult));

        assertTrue(exception.getMessage().contains("Validation errors:---> name-->  Name is required;"));
    }

    @Test
    void testGetToken_Success() throws InvalidCredentialsException {
        AuthRequest authRequest = new AuthRequest("username", "password");
        Authentication authentication = mock(Authentication.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authService.generateToken("username")).thenReturn("GeneratedToken");

        String result = authController.getToken(authRequest);

        assertEquals("GeneratedToken", result);
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, times(1)).generateToken("username");
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAuthRequests")
    void testGetToken_InvalidAuthRequest(AuthRequest authRequest, String expectedMessage) {
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                authController.getToken(authRequest));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidAuthRequests() {
        return Stream.of(
                Arguments.of(new AuthRequest(null, "password"), "Authentication failed: null (username is missing)"),
                Arguments.of(new AuthRequest("username", null), "Authentication failed: null (password is missing)"),
                Arguments.of(null, "Authentication failed: null")
        );
    }

    // Test case for getToken method - invalid credentials
    @Test
    void testGetToken_InvalidCredentials() {
        AuthRequest authRequest = new AuthRequest("username", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                authController.getToken(authRequest));

        assertEquals("Invalid Access! User Doesn't exist or credentials are incorrect.", exception.getMessage());
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = "validToken";
        doNothing().when(authService).validateToken(token);

        String response = authController.validateToken(token);

        assertEquals("Token is Valid", response);
        verify(authService).validateToken(token);
    }
    // Test case for getToken method - authRequest is null
    @Test
    void testGetToken_AuthRequestIsNull() {
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () ->
                authController.getToken(null));

        assertEquals("Authentication failed: null", exception.getMessage());
    }

    @Test
    void testGetToken_AuthenticationSuccess_IsAuthenticatedTrue() throws InvalidCredentialsException {
        AuthRequest authRequest = new AuthRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authService.generateToken("username")).thenReturn("GeneratedToken");

        String token = authController.getToken(authRequest);

        assertEquals("GeneratedToken", token);
        verify(authService).generateToken("username");
    }

    // Test case for getToken method - authentication failed (isAuthenticated() returns false)
    @Test
    void testGetToken_AuthenticationFails_IsAuthenticatedFalse() throws InvalidCredentialsException {
        AuthRequest authRequest = new AuthRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);  // Simulating failed authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String token = authController.getToken(authRequest);

        assertEquals("", token);  // Should return empty string as token since authentication failed
        verify(authService, never()).generateToken(anyString());
    }

}
