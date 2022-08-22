package com.github.arhor.simple.expense.tracker.data.repository;

import lombok.val;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalUserRepositoryTest extends RepositoryTestBase {

    @Test
    void should_return_an_existing_internal_user_by_username() {
        // given
        val expectedUser = createPersistedTestUser();

        // when
        val result = userRepository.findInternalUserByUsername(expectedUser.username());

        // then
        assertThat(result)
            .isNotEmpty()
            .contains(expectedUser);
    }

    @Test
    void should_return_an_empty_optional_by_a_not_existing_user_username() {
        // given
        val username = "not-exists";

        // when
        val result = userRepository.findInternalUserByUsername(username);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_an_existing_internal_user_by_external_id_and_external_provider() {
        // given
        val expectedUser = createPersistedTestUser();
        val externalId = expectedUser.externalId();
        val externalProvider = expectedUser.externalProvider();

        // when
        val result = userRepository.findByExternalIdAndProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isNotEmpty()
            .get()
            .satisfies(
                user -> {
                    assertThat(user.id())
                        .as("id")
                        .isEqualTo(expectedUser.id());
                },
                user -> {
                    assertThat(user.username())
                        .as("username")
                        .isEqualTo(expectedUser.username());
                },
                user -> {
                    assertThat(user.currency())
                        .as("currency")
                        .isEqualTo(expectedUser.currency());
                }
            );
    }

    @Test
    void should_return_an_empty_optional_by_a_not_existing_user_external_id_and_external_provider() {
        // given
        val externalId = "not-existing-id";
        val externalProvider = "not-existing-provider";

        // when
        val result = userRepository.findByExternalIdAndProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isEmpty();
    }

    @Test
    void should_return_true_for_an_existing_internal_user_by_username() {
        // given
        val username = createPersistedTestUser().username();

        // when
        val result = userRepository.existsByUsername(username);

        // then
        assertThat(result)
            .isTrue();
    }

    @Test
    void should_return_false_for_a_not_existing_internal_user_by_username() {
        // given
        val username = "not-exists";

        // when
        val result = userRepository.existsByUsername(username);

        // then
        assertThat(result)
            .isFalse();
    }

    @Test
    void should_return_true_for_an_existing_internal_user_by_external_id_and_external_provider() {
        // given
        val expectedUser = createPersistedTestUser();
        val externalId = expectedUser.externalId();
        val externalProvider = expectedUser.externalProvider();

        // when
        val result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isTrue();
    }

    @Test
    void should_return_false_for_a_not_existing_user_external_id_and_external_provider() {
        // given
        val externalId = "not-existing-id";
        val externalProvider = "not-existing-provider";

        // when
        val result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);

        // then
        assertThat(result)
            .isFalse();
    }

    @Test
    void should_properly_audit_entity_on_save() {
        // given
        val user = createTestUser();

        // when
        val createdUser = userRepository.save(user);
        val updatedUser = userRepository.save(createdUser.toBuilder().password("updated").build());

        // then
        assertThat(createdUser)
            .isNotNull()
            .satisfies(
                it -> {
                    assertThat(it.created())
                        .isNotNull();
                },
                it -> {
                    assertThat(it.updated())
                        .isNull();
                }
            );
        assertThat(updatedUser)
            .isNotNull()
            .satisfies(
                it -> {
                    assertThat(it.created())
                        .isNotNull()
                        .isEqualTo(createdUser.created());
                },
                it -> {
                    assertThat(it.updated())
                        .isNotNull()
                        .isAfter(it.created());
                }
            );
    }
}
