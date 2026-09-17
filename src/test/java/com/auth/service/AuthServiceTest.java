package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.RegisterRequest;
import com.auth.model.User;
import com.auth.store.UserStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl (business logic layer).
 * Uses Mockito to isolate AuthService from its dependencies.
 * Mocks interfaces (UserStoreRepository, PasswordEncoder) for Java 25 compatibility.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserStoreRepository userStore;          // interface → mockable on Java 25

    @Mock
    private PasswordEncoder passwordEncoder;        // interface → mockable on Java 25

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("mypassword");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("mypassword");
    }

    // ─────────────────────────────────────────────
    // register() tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("register: Should save new user when email is not taken")
    void register_shouldSaveUser_whenEmailIsNew() {
        when(userStore.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("mypassword")).thenReturn("$2a$hashed");

        assertThatCode(() -> authService.register(registerRequest))
                .doesNotThrowAnyException();

        verify(userStore, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register: Should hash the password before saving")
    void register_shouldHashPassword_beforeSaving() {
        when(userStore.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("mypassword")).thenReturn("$2a$hashed_password");

        authService.register(registerRequest);

        // Verify password was encoded, not stored as plain text
        verify(passwordEncoder, times(1)).encode("mypassword");
        verify(userStore).save(argThat(user ->
                user.getHashedPassword().equals("$2a$hashed_password")
        ));
    }

    @Test
    @DisplayName("register: Should throw when email is already registered")
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userStore.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already registered");

        verify(userStore, never()).save(any());
    }

    @Test
    @DisplayName("register: Should normalize email to lowercase before saving")
    void register_shouldNormalizeEmailToLowercase() {
        registerRequest.setEmail("User@EXAMPLE.COM");
        when(userStore.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");

        authService.register(registerRequest);

        verify(userStore).save(argThat(user ->
                user.getEmail().equals("user@example.com")
        ));
    }

    // ─────────────────────────────────────────────
    // login() tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("login: Should succeed with correct email and password")
    void login_shouldSucceed_whenCredentialsAreCorrect() {
        User storedUser = new User("test@example.com", "$2a$hashed");
        when(userStore.findByEmail("test@example.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("mypassword", "$2a$hashed")).thenReturn(true);

        assertThatCode(() -> authService.login(loginRequest))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("login: Should throw when email is not registered")
    void login_shouldThrow_whenEmailNotFound() {
        when(userStore.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("login: Should throw when password does not match")
    void login_shouldThrow_whenPasswordIsWrong() {
        User storedUser = new User("test@example.com", "$2a$hashed");
        when(userStore.findByEmail("test@example.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("mypassword", "$2a$hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("login: Should use case-insensitive email lookup")
    void login_shouldNormalizeEmailToLowercase() {
        loginRequest.setEmail("TEST@EXAMPLE.COM");
        User storedUser = new User("test@example.com", "$2a$hashed");
        when(userStore.findByEmail("test@example.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThatCode(() -> authService.login(loginRequest))
                .doesNotThrowAnyException();
    }
}
