package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper
import io.mockk.called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

@Suppress("ClassName")
@ExtendWith(MockKExtension::class)
internal class UserServiceImplTest {

    @MockK
    private lateinit var userRepository: InternalUserRepository

    @MockK
    private lateinit var userMapper: InternalUserMapper

    @MockK
    private lateinit var oAuth2Authentication: OAuth2AuthenticationToken

    @MockK
    private lateinit var usernamePasswordAuthentication: UsernamePasswordAuthenticationToken

    @MockK
    private lateinit var internalUserProjection: CompactInternalUserProjection

    @MockK
    private lateinit var userResponseDTO: UserResponseDTO

    @InjectMockKs
    private lateinit var userService: UserServiceImpl

    @Nested
    inner class `UserService # loadUserByUsername` {
        @Test
        fun `should return expected user details for an existing user`() {
            // given
            val expectedUsername = "test-username"
            val expectedPassword = "test-password"
            val expectedAuthorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

            every { userRepository.findInternalUserByUsername(username = any()) } returns InternalUser(
                id = 1L,
                username = expectedUsername,
                password = expectedPassword,
                currency = "USD"
            )

            // when
            val actualUser = userService.loadUserByUsername(expectedUsername)

            // then
            verify(exactly = 1) { userRepository.findInternalUserByUsername(username = expectedUsername) }

            assertThat(actualUser)
                .returns(expectedUsername, from { it.username })
                .returns(expectedPassword, from { it.password })
                .returns(expectedAuthorities, from { it.authorities })
        }

        @Test
        fun `should throw exception for a not existing user`() {
            // given
            val expectedUsername = "test-username"

            every { userRepository.findInternalUserByUsername(username = any()) } returns null

            // when
            val exception = catchThrowable { userService.loadUserByUsername(expectedUsername) }

            // then
            verify(exactly = 1) { userRepository.findInternalUserByUsername(username = expectedUsername) }

            assertThat(exception)
                .isNotNull
                .isInstanceOf(UsernameNotFoundException::class.java)
                .hasMessageContaining(expectedUsername)
        }
    }

    @Nested
    inner class `UserService # createNewUser` {
        @Test
        fun `should successfully a create new user`() {
            // given
            val expectedId = -1L
            val expectedUsername = "username"
            val expectedPassword = "password"
            val expectedCurrency = "USD"

            every { userRepository.existsByUsername(any()) } returns false
            every { userMapper.mapToUser(any()) } answers {
                arg<UserRequestDTO>(0).let {
                    InternalUser(
                        username = it.username,
                        password = it.password,
                        currency = it.currency!!,
                    )
                }
            }
            every { userRepository.save(any()) } answers {
                arg<InternalUser>(0).copy(
                    id = expectedId
                )
            }
            every { userMapper.mapToResponse(any<InternalUser>()) } answers {
                arg<InternalUser>(0).let {
                    UserResponseDTO(
                        it.id,
                        it.username,
                        it.currency,
                    )
                }
            }

            // when
            val result = userService.createNewUser(
                UserRequestDTO(
                    expectedUsername,
                    expectedPassword,
                    expectedCurrency
                )
            )

            // then
            verify(exactly = 1) { userRepository.existsByUsername(username = expectedUsername) }
            verify(exactly = 1) { userMapper.mapToUser(request = any()) }
            verify(exactly = 1) { userRepository.save(any()) }
            verify(exactly = 1) { userMapper.mapToResponse(entity = any()) }

            confirmVerified(userRepository, userMapper)

            assertThat(result)
                .returns(expectedId, from { it.id })
                .returns(expectedUsername, from { it.username })
                .returns(expectedCurrency, from { it.currency })
        }

        @Test
        fun `should throw EntityDuplicateException creating new user with existing username`() {
            // given
            val expectedUsername = "username"

            every { userRepository.existsByUsername(any()) } returns true

            // when
            val result = catchThrowable {
                userService.createNewUser(
                    UserRequestDTO(
                        expectedUsername,
                        "password",
                        "USD",
                    )
                )
            }

            // then
            verify(exactly = 1) { userRepository.existsByUsername(username = expectedUsername) }

            confirmVerified(userRepository, userMapper)

            assertThat(result)
                .isInstanceOf(EntityDuplicateException::class.java)
        }
    }

    @Nested
    inner class `UserService # createNewUserIfNecessary` {
        @Test
        fun `should create internal user for an OAuth2 authentication which is not present in repository`() {
            // given
            val internalUserCapturingSlot = slot<InternalUser>()
            val expectedCurrency = "USD"
            val expectedExternalId = "external-id"
            val expectedExternalProvider = "external-provider"

            every { oAuth2Authentication.name } returns expectedExternalId
            every { oAuth2Authentication.authorizedClientRegistrationId } returns expectedExternalProvider
            every { userRepository.existsByExternalIdAndExternalProvider(any(), any()) } returns false
            every { userRepository.save(any()) } answers { arg(0) }

            // when
            userService.createInternalUserIfNecessary(oAuth2Authentication)

            // then
            verify {
                userRepository.existsByExternalIdAndExternalProvider(
                    expectedExternalId,
                    expectedExternalProvider
                )
            }
            verify { userRepository.save(capture(internalUserCapturingSlot)) }

            confirmVerified(userRepository)

            assertThat(internalUserCapturingSlot.captured)
                .returns(expectedCurrency, from { it.currency })
                .returns(expectedExternalId, from { it.externalId })
                .returns(expectedExternalProvider, from { it.externalProvider })
        }

        @Test
        fun `should do nothing for unknown authentication object`() {
            // given
            val unknownAuthentication = mockk<Authentication>()

            // when
            userService.createInternalUserIfNecessary(unknownAuthentication)

            // then
            verify { userRepository wasNot called }
        }
    }
}
