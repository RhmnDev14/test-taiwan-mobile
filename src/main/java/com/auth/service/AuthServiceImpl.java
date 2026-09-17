package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.model.User;
import com.auth.store.UserStoreRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthServiceImpl handles the core business logic for user authentication.
 *
 * Responsibilities:
 *  - Registering new users (with duplicate email check + BCrypt hashing)
 *  - Authenticating existing users (email lookup + BCrypt comparison)
 *
 * This layer does NOT deal with HTTP details — that is the Controller's job.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserStoreRepository userStore;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserStoreRepository userStore, PasswordEncoder passwordEncoder) {
        this.userStore = userStore;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * @throws IllegalArgumentException if the email is already in use.
     */
    @Override
    public void register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase();

        if (userStore.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered: " + email);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(email, hashedPassword);
        userStore.save(newUser);
    }

    /**
     * Authenticates a user.
     *
     * @throws IllegalArgumentException if email not found or password doesn't match.
     */
    @Override
    public void login(LoginRequest request) {
        String email = request.getEmail().toLowerCase();

        User user = userStore.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getHashedPassword());

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
