package com.github.arhor.simple.expense.tracker.data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.arhor.simple.expense.tracker.data.repository.TestUtils.createPersistedTestUser;
import static com.github.arhor.simple.expense.tracker.data.repository.TestUtils.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends RepositoryTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void should_return_an_existing_internal_user_by_username() {
        // given
        var expectedUser = createPersistedTestUser(userRepository);

        // when
        var result = userRepository.findInternalUserByUsername(expectedUser.getUsername());

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
        var result = userRepository.findInternalUserByUsername(username);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_an_existing_internal_user_by_external_id_and_external_provider() {
        // given
        var expectedUser = createPersistedTestUser(userRepository);
        var externalId = expectedUser.getExternalId();
        var externalProvider = expectedUser.getExternalProvider();

        // when
        var result = userRepository.findByExternalIdAndProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isNotEmpty()
            .get()
            .satisfies(
                user -> {
                    assertThat(user.id())
                        .as("id")
                        .isEqualTo(expectedUser.getId());
                },
                user -> {
                    assertThat(user.username())
                        .as("username")
                        .isEqualTo(expectedUser.getUsername());
                },
                user -> {
                    assertThat(user.currency())
                        .as("currency")
                        .isEqualTo(expectedUser.getCurrency());
                }
            );
    }

    @Test
    void should_return_an_empty_optional_by_a_not_existing_user_external_id_and_external_provider() {
        // given
        var externalId = "not-existing-id";
        var externalProvider = "not-existing-provider";

        // when
        var result = userRepository.findByExternalIdAndProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_true_for_an_existing_internal_user_by_username() {
        // given
        var username = createPersistedTestUser(userRepository).getUsername();

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
        var expectedUser = createPersistedTestUser(userRepository);
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
