package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;

/**
 * Contract for authentication operations.
 * Using an interface allows clean dependency injection and makes
 * mocking in unit tests reliable across all Java versions.
 */
public interface AuthService {

    /**
     * Registers a new user account.
     * @throws IllegalArgumentException if email is already taken.
     */
    void register(RegisterRequest request);

    /**
     * Authenticates an existing user.
     * @throws IllegalArgumentException if credentials are invalid.
     */
    void login(LoginRequest request);
}
