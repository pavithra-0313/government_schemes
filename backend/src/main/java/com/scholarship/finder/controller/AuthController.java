package com.scholarship.finder.controller;

import com.scholarship.finder.dto.ApiResponse;
import com.scholarship.finder.dto.AuthRequest;
import com.scholarship.finder.dto.AuthResponse;
import com.scholarship.finder.dto.RegisterRequest;
import com.scholarship.finder.entity.User;
import com.scholarship.finder.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for student and admin registration, login, and identity verification")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new student or admin account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password to receive JWT Bearer token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user details")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        Long userId = (Long) authentication.getCredentials();
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}

