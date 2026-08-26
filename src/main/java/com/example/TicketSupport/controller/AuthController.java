package com.example.TicketSupport.controller;

import com.example.TicketSupport.annotation.JwtRequired;
import com.example.TicketSupport.dto.*;
import com.example.TicketSupport.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@Valid @RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshAccessToken(request.getRefreshToken()));
    }

    @JwtRequired
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok((Page<UserResponse>) authService.getAll(pageable));
    }

}
