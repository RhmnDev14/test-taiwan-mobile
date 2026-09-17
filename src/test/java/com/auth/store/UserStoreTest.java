package com.auth.store;

import com.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserStore (in-memory repository layer).
 * Tests storage, retrieval, and case-insensitive email handling.
 */
@DisplayName("UserStore Tests")
class UserStoreTest {

    private UserStore userStore;

    @BeforeEach
    void setUp() {
        userStore = new UserStore();
    }

    @Test
    @DisplayName("Should save and retrieve a user by email")
    void shouldSaveAndFindUser() {
        User user = new User("alice@example.com", "hashedPassword123");

        userStore.save(user);
        Optional<User> found = userStore.findByEmail("alice@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
        assertThat(found.get().getHashedPassword()).isEqualTo("hashedPassword123");
    }

    @Test
    @DisplayName("Should return empty Optional when email does not exist")
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> found = userStore.findByEmail("notfound@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find user regardless of email case (case-insensitive)")
    void shouldFindUserCaseInsensitive() {
        User user = new User("Bob@Example.COM", "hashedPassword456");
        userStore.save(user);

        // Query with different case
        assertThat(userStore.findByEmail("bob@example.com")).isPresent();
        assertThat(userStore.findByEmail("BOB@EXAMPLE.COM")).isPresent();
    }

    @Test
    @DisplayName("Should return true when email already exists")
    void shouldReturnTrueForExistingEmail() {
        userStore.save(new User("carol@example.com", "hash"));

        assertThat(userStore.existsByEmail("carol@example.com")).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void shouldReturnFalseForMissingEmail() {
        assertThat(userStore.existsByEmail("ghost@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should overwrite user when saving with same email")
    void shouldOverwriteExistingUser() {
        userStore.save(new User("dave@example.com", "oldHash"));
        userStore.save(new User("dave@example.com", "newHash"));

        Optional<User> found = userStore.findByEmail("dave@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getHashedPassword()).isEqualTo("newHash");
    }
}
