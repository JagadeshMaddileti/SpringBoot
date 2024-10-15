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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewUser_ShouldReturnSavedUserId_WhenValidUser() throws Exception {
        UserCredentials user = new UserCredentials(1,"user", "user@example.com", "password");
        when(authService.saveUser(any(UserCredentials.class))).thenReturn("User saved");

        String result = authController.addNewUser(user, mock(BindingResult.class));
        assertEquals("User saved", result);

        verify(authService, times(1)).saveUser(any(UserCredentials.class));
    }

    @Test
    void addNewUser_ShouldThrowInvalidCredentialsException_WhenValidationErrors() {
        UserCredentials user = new UserCredentials(1,"", "", "password");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("user", "email", "Email is required")));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authController.addNewUser(user, bindingResult));

        assertEquals("Validation errors:---> email-->  Email is required;   ", exception.getMessage());
    }

    @Test
    void getToken_ShouldReturnToken_WhenValidCredentials() throws Exception {
        AuthRequest authRequest = new AuthRequest("user", "password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user", "password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authService.generateToken("user")).thenReturn("token");

        String result = authController.getToken(authRequest);

        assertEquals("token", result);
        verify(authService, times(1)).generateToken("user");
    }
    @Test
    void getToken_ShouldThrowInvalidCredentialsException_WhenInvalidCredentials() {
        AuthRequest authRequest = new AuthRequest("user", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authController.getToken(authRequest));

        assertEquals("Invalid Access! User Doesn't exist or credentials are incorrect.", exception.getMessage());
    }

    @Test
    void getToken_ShouldThrowInvalidCredentialsException_WhenCredentialsAreNull() {
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authController.getToken(null));

        assertEquals("Authentication failed: null", exception.getMessage());
    }

    @Test
    void getToken_ShouldThrowInvalidCredentialsException_WhenBadCredentials() {
        AuthRequest authRequest = new AuthRequest("user", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authController.getToken(authRequest));

        assertEquals("Invalid Access! User Doesn't exist or credentials are incorrect.", exception.getMessage());
    }

    @Test
    void validateToken_ShouldReturnValidMessage_WhenTokenIsValid() {
        String token = "validToken";
        doNothing().when(authService).validateToken(token);

        String result = authController.validateToken(token);
        assertEquals("Token is Valid", result);

        verify(authService, times(1)).validateToken(token);
    }

    @Test
    void validateToken_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = "invalidToken";
        doThrow(new InvalidCredentialsException("Invalid token")).when(authService).validateToken(invalidToken);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authController.validateToken(invalidToken));

        assertEquals("Invalid token", exception.getMessage());
    }
}
