package com.auth.controller;

import com.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for AuthController using MockMvc.
 *
 * Uses @SpringBootTest + @AutoConfigureMockMvc to load the full context
 * (required for proper Security + Validation integration on Java 25).
 * AuthService is mocked via @MockBean so only HTTP concerns are tested.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mocking the AuthService interface — works on all Java versions
    @MockBean
    private AuthService authService;

    // ─────────────────────────────────────────────
    // POST /register tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /register: Should return 201 on successful registration")
    void register_shouldReturn201_whenSuccessful() throws Exception {
        doNothing().when(authService).register(any());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @DisplayName("POST /register: Should return 400 when email is already taken")
    void register_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        doThrow(new IllegalArgumentException("Email is already registered: alice@example.com"))
                .when(authService).register(any());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already registered: alice@example.com"));
    }

    @Test
    @DisplayName("POST /register: Should return 400 when email format is invalid")
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "not-a-valid-email",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("email: Email format is invalid"));
    }

    @Test
    @DisplayName("POST /register: Should return 400 when password is too short")
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": "abc"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("password: Password must be at least 6 characters"));
    }

    @Test
    @DisplayName("POST /register: Should return 400 when email is blank")
    void register_shouldReturn400_whenEmailIsBlank() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─────────────────────────────────────────────
    // POST /login tests
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /login: Should return 200 on successful login")
    void login_shouldReturn200_whenCredentialsAreCorrect() throws Exception {
        doNothing().when(authService).login(any());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("POST /login: Should return 401 when credentials are invalid")
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        doThrow(new IllegalArgumentException("Invalid email or password"))
                .when(authService).login(any());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /login: Should return 400 when email format is invalid")
    void login_shouldReturn400_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "bademail",
                                    "password": "securepass"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /login: Should return 400 when password is blank")
    void login_shouldReturn400_whenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "alice@example.com",
                                    "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
