package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.arhor.simple.expense.tracker.config.DatabaseConfig;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = DatabaseConfig.class)
abstract class RepositoryTestBase {

    @Autowired
    private UserRepository userRepository;

    static JdbcDatabaseContainer<?> createDatabaseContainer() {
        return new PostgreSQLContainer<>("postgres:12");
    }

    static void registerDatasource(final DynamicPropertyRegistry registry, final JdbcDatabaseContainer<?> db) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    protected InternalUser createTestUser() {
        return createTestUser(0);
    }

    protected InternalUser createTestUser(final Number number) {
        return InternalUser.builder()
            .username("test-user-username-" + number)
            .password("test-user-password-" + number)
            .currency("USD")
            .externalId(UUID.randomUUID().toString())
            .externalProvider("test")
            .build();
    }

    protected InternalUser createPersistedTestUser() {
        return createPersistedTestUser(0);
    }

    protected InternalUser createPersistedTestUser(final Number number) {
        return userRepository.save(createTestUser(number));
    }
}
