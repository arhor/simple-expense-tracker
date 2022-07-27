package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.UUID;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

final class TestUtils {

    private TestUtils() { /* should not be instantiated*/ }

    static InternalUser createTestUser() {
        return createTestUser(0);
    }

    static InternalUser createTestUser(final Number number) {
        return InternalUser.builder()
            .username("test-user-username-" + number)
            .password("test-user-password-" + number)
            .currency("USD")
            .externalId(UUID.randomUUID().toString())
            .externalProvider("test")
            .build();
    }

    static InternalUser createPersistedTestUser(final UserRepository userRepository) {
        return createPersistedTestUser(userRepository, 0);
    }

    static InternalUser createPersistedTestUser(final UserRepository userRepository, final Number number) {
        return userRepository.save(createTestUser(number));
    }
}
