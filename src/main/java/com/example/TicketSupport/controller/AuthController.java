package com.example.TicketSupport.controller;

import com.example.TicketSupport.annotation.JwtRequired;
import com.example.TicketSupport.dto.*;
import com.example.TicketSupport.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request.getRefreshToken()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok((Page<UserResponse>) authService.getAll(pageable));
    }

    @PatchMapping("/updatepassword")
    public void updatePassword(@Valid @RequestBody PasswordUpdateRequest request, Authentication authentication) {
        authService.updatePassword(request, authentication);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequest request, HttpServletRequest httpServletRequest) {

        authService.logout(request, httpServletRequest);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/unlock-user")
    public ResponseEntity<Void> unlockUser(@Valid @RequestBody UnlockUserRequest request) {
        authService.unlockUser(request);
        return ResponseEntity.noContent().build();
    }

}
