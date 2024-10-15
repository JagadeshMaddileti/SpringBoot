package com.springboot.security_service.controller;


import com.springboot.security_service.dto.AuthRequest;
import com.springboot.security_service.exception.InvalidCredentialsException;
import com.springboot.security_service.model.UserCredentials;
import com.springboot.security_service.service.AuthService;
import com.springboot.security_service.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.AUTH_BASE_URL)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping(Constants.REGISTER)
    public String addNewUser(@RequestBody UserCredentials user, BindingResult bindingResult) throws InvalidCredentialsException {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors:---> ");
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField()).append("-->  ").append(error.getDefaultMessage()).append(";   ")
            );
            throw new InvalidCredentialsException(errorMessage.toString());
        }
        return authService.saveUser(user);
    }

    @PostMapping(Constants.TOKEN)
    public String getToken(@RequestBody AuthRequest authRequest) throws InvalidCredentialsException {
        if (authRequest == null || authRequest.getUsername() == null || authRequest.getPassword() == null) {
            throw new InvalidCredentialsException("Authentication failed: null");
        }
        String token = "";
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                token= authService.generateToken(authRequest.getUsername());
            }
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid Access! User Doesn't exist or credentials are incorrect.");
        }
        return token;
    }


    @GetMapping(Constants.VALIDATE)
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is Valid";
    }
}