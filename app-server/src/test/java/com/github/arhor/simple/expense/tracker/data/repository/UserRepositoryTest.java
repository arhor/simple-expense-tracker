package com.github.arhor.simple.expense.tracker.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends RepositoryTestBase {

    @Container
    private static final JdbcDatabaseContainer<?> db = createDatabaseContainer();

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registerDatasource(registry, db);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_return_an_existing_internal_user_by_username() {
        // given
        var expectedUser = createPersistedTestUser();

        // when
        var result = userRepository.findByUsername(expectedUser.getUsername());

        // then
        assertThat(result)
            .isNotEmpty()
            .contains(expectedUser);
    }

    @Test
    void should_return_an_empty_optional_by_a_not_existing_user_username() {
        // given
        var username = "not-exists";

        // when
        var result = userRepository.findByUsername(username);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_an_existing_internal_user_by_external_id_and_external_provider() {
        // given
        var expectedUser = createPersistedTestUser();
        var externalId = expectedUser.getExternalId();
        var externalProvider = expectedUser.getExternalProvider();

        // when
        var result = userRepository.findByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isNotEmpty()
            .contains(expectedUser);
    }

    @Test
    void should_return_an_empty_optional_by_a_not_existing_user_external_id_and_external_provider() {
        // given
        var externalId = "not-existing-id";
        var externalProvider = "not-existing-provider";

        // when
        var result = userRepository.findByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_true_for_an_existing_internal_user_by_username() {
        // given
        var username = createPersistedTestUser().getUsername();

        // when
        var result = userRepository.existsByUsername(username);

        // then
        assertThat(result)
            .isTrue();
    }

    @Test
    void should_return_false_for_a_not_existing_internal_user_by_username() {
        // given
        var username = "not-exists";

        // when
        var result = userRepository.existsByUsername(username);

        // then
        assertThat(result)
            .isFalse();
    }

    @Test
    void should_return_true_for_an_existing_internal_user_by_external_id_and_external_provider() {
        // given
        var expectedUser = createPersistedTestUser();
        var externalId = expectedUser.getExternalId();
        var externalProvider = expectedUser.getExternalProvider();

        // when
        var result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isTrue();
    }

    @Test
    void should_return_false_for_a_not_existing_user_external_id_and_external_provider() {
        // given
        var externalId = "not-existing-id";
        var externalProvider = "not-existing-provider";

        // when
        var result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isFalse();
    }

    @Test
    void should_properly_audit_entity_on_save() {
        // given
        var user = createTestUser();

        // when
        var createdUser = userRepository.save(user);
        var updatedUser = userRepository.save(createdUser.toBuilder().password("updated").build());

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
}
