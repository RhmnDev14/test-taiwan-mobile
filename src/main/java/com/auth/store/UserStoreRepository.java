package com.auth.store;

import com.auth.model.User;

import java.util.Optional;

/**
 * Contract for the user storage layer.
 * Using an interface allows clean mocking in unit tests.
 */
public interface UserStoreRepository {

    void save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
