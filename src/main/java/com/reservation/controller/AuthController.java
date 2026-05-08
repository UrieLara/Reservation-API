package com.reservation.controller;

import com.reservation.model.dto.AuthRequest;
import com.reservation.model.dto.AuthResponse;
import com.reservation.model.dto.RegisterRequest;
import com.reservation.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro y login de usuarios")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}