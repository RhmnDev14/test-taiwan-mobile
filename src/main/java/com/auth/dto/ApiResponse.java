package com.auth.dto;

/**
 * Generic API response wrapper used for all endpoints.
 * Carries a success flag and a human-readable message.
 */
public class ApiResponse {

    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
