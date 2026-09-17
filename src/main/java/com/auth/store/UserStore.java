package com.auth.store;

import com.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for users, using a thread-safe ConcurrentHashMap.
 * Keyed by email (lowercase) to ensure uniqueness regardless of case.
 *
 * This acts as the "Repository" layer — responsible only for
 * storing and retrieving User objects.
 */
@Repository
public class UserStore implements UserStoreRepository {

    // Thread-safe in-memory map: email -> User
    private final Map<String, User> storage = new ConcurrentHashMap<>();

    /**
     * Saves a user. Overwrites if the email already exists.
     */
    public void save(User user) {
        storage.put(user.getEmail().toLowerCase(), user);
    }

    /**
     * Finds a user by their email address (case-insensitive).
     */
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(storage.get(email.toLowerCase()));
    }

    /**
     * Checks whether an email is already registered.
     */
    public boolean existsByEmail(String email) {
        return storage.containsKey(email.toLowerCase());
    }
}
