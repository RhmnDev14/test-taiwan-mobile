package com.auth.controller;

import com.auth.dto.ApiResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController exposes the REST API endpoints for authentication.
 *
 * Endpoints:
 *   POST /register  — creates a new user account
 *   POST /login     — authenticates an existing user
 *
 * This layer only handles HTTP concerns (request/response mapping,
 * status codes). Business logic is delegated to AuthService.
 */
@RestController
@RequestMapping("/")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user account.
     *
     * Returns 201 Created on success.
     * Returns 400 Bad Request if the email is already taken or validation fails.
     */
    @Operation(summary = "Register a new user", description = "Creates a new account using email and password. Password is stored as a BCrypt hash.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email already registered or validation failed")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Registration successful"));
    }

    /**
     * Authenticate an existing user.
     *
     * Returns 200 OK on success.
     * Returns 401 Unauthorized if credentials are invalid.
     */
    @Operation(summary = "Login with email and password", description = "Authenticates a user by comparing the provided password against the stored BCrypt hash.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid email or password"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        authService.login(request);
        return ResponseEntity
                .ok(new ApiResponse(true, "Login successful"));
    }
}

