package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

@ExtendWith(MockKExtension::class)
internal class CustomUserDetailsServiceTest {

    @MockK
    private lateinit var internalUserRepository: InternalUserRepository

    @InjectMockKs
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @Test
    fun `should return expected user details for an existing user`() {
        // given
        val expectedUsername = "test-username"
        val expectedPassword = "test-password"
        val expectedAuthorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        every { internalUserRepository.findInternalUserByUsername(username = any()) } returns InternalUser(
            username = expectedUsername,
            password = expectedPassword,
            currency = "USD"
        )

        // when
        val actualUser = customUserDetailsService.loadUserByUsername(expectedUsername)

        // then
        verify(exactly = 1) { internalUserRepository.findInternalUserByUsername(username = expectedUsername) }

        assertThat(actualUser)
            .returns(expectedUsername, from { it.username })
            .returns(expectedPassword, from { it.password })
            .returns(expectedAuthorities, from { it.authorities })
    }

    @Test
    fun `should throw exception for a not existing user`() {
        // given
        val expectedUsername = "test-username"

        every { internalUserRepository.findInternalUserByUsername(username = any()) } returns null

        // when
        val exception = catchThrowable { customUserDetailsService.loadUserByUsername(expectedUsername) }

        // then
        verify(exactly = 1) { internalUserRepository.findInternalUserByUsername(username = expectedUsername) }

        assertThat(exception)
            .isNotNull
            .isInstanceOf(UsernameNotFoundException::class.java)
            .hasMessageContaining(expectedUsername)
    }
}
