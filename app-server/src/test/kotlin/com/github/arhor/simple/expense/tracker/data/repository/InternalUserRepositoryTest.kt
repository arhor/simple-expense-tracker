package com.github.arhor.simple.expense.tracker.data.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Consumer

internal class InternalUserRepositoryTest : RepositoryTestBase() {

    @Test
    fun `should return an existing internal user by username`() {
        // given
        val expectedUser = createPersistedTestUser()

        // when
        val result = userRepository.findInternalUserByUsername(expectedUser.username!!)

        // then
        assertThat(result)
            .isNotNull
            .isEqualTo(expectedUser)
    }

    @Test
    fun `should return an empty optional by a not existing user username`() {
        // given
        val username = "not-exists"

        // when
        val result = userRepository.findInternalUserByUsername(username)

        // then
        assertThat(result)
            .isNull()
    }

    @Test
    fun `should return an existing internal user by external id and external provider`() {
        // given
        val expectedUser = createPersistedTestUser()
        val externalId = expectedUser.externalId!!
        val externalProvider = expectedUser.externalProvider!!

        // when
        val result = userRepository.findByExternalIdAndProvider(externalId, externalProvider)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it!!.id)
                        .describedAs("id")
                        .isEqualTo(expectedUser.id)
                },
                {
                    assertThat(it!!.username)
                        .describedAs("username")
                        .isEqualTo(expectedUser.username)
                },
                {
                    assertThat(it!!.currency)
                        .describedAs("currency")
                        .isEqualTo(expectedUser.currency)
                }
            )
    }

    @Test
    fun `should return an empty optional by a not existing user external id and external provider`() {
        // given
        val externalId = "not-existing-id"
        val externalProvider = "not-existing-provider"

        // when
        val result = userRepository.findByExternalIdAndProvider(externalId, externalProvider)

        // then
        assertThat(result)
            .isNull()
    }

    @Test
    fun `should return true for an existing internal user by username`() {
        // given
        val username = createPersistedTestUser().username!!

        // when
        val result = userRepository.existsByUsername(username)

        // then
        assertThat(result)
            .isTrue
    }

    @Test
    fun `should return false for a not existing internal user by username`() {
        // given
        val username = "not-exists"

        // when
        val result = userRepository.existsByUsername(username)

        // then
        assertThat(result)
            .isFalse
    }

    @Test
    fun `should return true for an existing internal user by external id and external provider`() {
        // given
        val expectedUser = createPersistedTestUser()
        val externalId = expectedUser.externalId!!
        val externalProvider = expectedUser.externalProvider!!

        // when
        val result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider)

        // then
        assertThat(result)
            .isTrue
    }

    @Test
    fun `should return false for a not existing user external id and external provider`() {
        // given
        val externalId = "not-existing-id"
        val externalProvider = "not-existing-provider"

        // when
        val result = userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider)

        // then
        assertThat(result)
            .isFalse
    }

    @Test
    fun `should properly audit entity on save`() {
        // given
        val user = createTestUser()

        // when
        val createdUser = userRepository.save(user)
        val updatedUser = userRepository.save(createdUser.copy(password = "updated"))

        // then
        assertThat(createdUser)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.created)
                        .isNotNull()
                },
                {
                    assertThat(it.updated)
                        .isNull()
                }
            )
        assertThat(updatedUser)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.created)
                        .isNotNull
                        .isEqualTo(createdUser.created)
                },
                {
                    assertThat(it.updated)
                        .isNotNull
                        .isAfter(it.created)
                }
            )
    }
}
