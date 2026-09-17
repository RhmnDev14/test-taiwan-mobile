package com.auth.exception;

import com.auth.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler that translates exceptions into consistent
 * ApiResponse JSON payloads with appropriate HTTP status codes.
 *
 * Handles:
 *  - Validation errors  → 400 Bad Request
 *  - Business rule errors (duplicate email, bad credentials) → 400 / 401
 *  - Unexpected errors  → 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles @Valid annotation failures (e.g. blank email, short password).
     * Collects all field errors into a single readable message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(false, errorMessage));
    }

    /**
     * Handles business rule violations thrown by AuthService:
     *  - Duplicate email during registration → 400
     *  - Invalid credentials during login   → 401
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage();

        // Return 401 for login credential errors to be semantically correct
        HttpStatus status = (message != null && message.startsWith("Invalid email or password"))
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(new ApiResponse(false, message));
    }

    /**
     * Catch-all for unexpected server errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "An unexpected error occurred"));
    }
}
