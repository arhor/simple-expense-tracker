package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.arhor.simple.expense.tracker.config.DatabaseConfig;
import com.github.arhor.simple.expense.tracker.data.model.InternalUser;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = DatabaseConfig.class)
class UserRepositoryTest {

    @Container
    private static final JdbcDatabaseContainer<?> db = new PostgreSQLContainer<>("postgres:12");

    @Autowired
    private UserRepository repository;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Test
    void should_find_internal_user_by_username() {
        // given
        var user = repository.save(createInternalUser());

        // when
        var actualUser = repository.findByUsername(user.getUsername());

        // then
        assertThat(actualUser)
            .isNotEmpty()
            .contains(user);
    }

    @Test
    void should_find_internal_user_by_external_id_and_provider() {
        // given
        var user = repository.save(createInternalUser());

        // when
        var actualUser =
            repository.findByExternalIdAndProvider(
                user.getExternalId(),
                user.getExternalProvider()
            );

        // then
        assertThat(actualUser)
            .isNotEmpty()
            .contains(user);
    }

    @Test
    void should_softly_delete_internal_user() {
        // given
        var user = repository.save(createInternalUser());

        // when
        var prevResult = repository.findById(user.getId());
        repository.delete(user);
        var currResult = repository.findById(user.getId());

        // then
        assertThat(prevResult)
            .contains(user);
        assertThat(currResult)
            .isEmpty();
    }

    @Test
    void should_softly_delete_internal_user_by_id() {
        // given
        var user = repository.save(createInternalUser());

        // when
        var prevResult = repository.findById(user.getId());
        repository.deleteById(user.getId());
        var currResult = repository.findById(user.getId());

        // then
        assertThat(prevResult)
            .contains(user);
        assertThat(currResult)
            .isEmpty();
    }

    @Test
    void should_properly_audit_entity_on_save() {
        // given
        var user = createInternalUser();

        // when
        var createdUser = repository.save(user);
        var updatedUser = repository.save(createdUser.toBuilder().password("updated").build());

        // then
        assertThat(createdUser.getCreated())
            .isNotNull();
        assertThat(createdUser.getUpdated())
            .isNull();

        assertThat(updatedUser.getCreated())
            .isNotNull()
            .isEqualTo(createdUser.getCreated());
        assertThat(updatedUser.getUpdated())
            .isNotNull()
            .isAfter(updatedUser.getCreated());
    }

    private InternalUser createInternalUser() {
        var user = new InternalUser();

        user.setUsername("username");
        user.setPassword("password");
        user.setCurrency("USD");
        user.setExternalId(UUID.randomUUID().toString());
        user.setExternalProvider("test");

        return user;
    }
}
