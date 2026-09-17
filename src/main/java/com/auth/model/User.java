package com.auth.model;

/**
 * Represents a registered user stored in memory.
 * Contains the user's email and their BCrypt-hashed password.
 */
public class User {

    private String email;
    private String hashedPassword;

    public User(String email, String hashedPassword) {
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
