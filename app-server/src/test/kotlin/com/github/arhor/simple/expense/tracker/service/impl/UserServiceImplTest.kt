package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

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

    @Test
    fun `should return an expected internal user id for a recognized OAuth2 authentication token`() {
        // given
        val expectedExternalId = "external-id"
        val expectedExternalProvider = "external-provider"
        val expectedInternalUserId = 1L

        every { oAuth2Authentication.name } returns expectedExternalId
        every { oAuth2Authentication.authorizedClientRegistrationId } returns expectedExternalProvider
        every { userRepository.findByExternalIdAndProvider(any(), any()) } returns internalUserProjection
        every { internalUserProjection.id } returns expectedInternalUserId

        // when
        val result = userService.determineUserId(oAuth2Authentication)

        // then
        verify(exactly = 1) { userRepository.findByExternalIdAndProvider(expectedExternalId, expectedExternalProvider) }

        assertThat(result)
            .isEqualTo(expectedInternalUserId)
    }

    @Test
    fun `should throw an exception for an unrecognized OAuth2 authentication token`() {
        // given
        val expectedExternalId = "external-id"
        val expectedExternalProvider = "external-provider"
        val expectedExceptionParams = arrayOf(
            "User",
            "externalId=${expectedExternalId}, externalProvider=${expectedExternalProvider}"
        )

        every { oAuth2Authentication.name } returns expectedExternalId
        every { oAuth2Authentication.authorizedClientRegistrationId } returns expectedExternalProvider
        every { userRepository.findByExternalIdAndProvider(any(), any()) } returns null

        // when
        val result = catchThrowable { userService.determineUserId(oAuth2Authentication) }

        // then
        verify(exactly = 1) { userRepository.findByExternalIdAndProvider(expectedExternalId, expectedExternalProvider) }

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    @Test
    fun `should return an expected internal user id for a recognized UsernamePassword authentication token`() {
        // given
        val expectedUsername = "username"
        val expectedInternalUserId = 1L

        every { usernamePasswordAuthentication.name } returns expectedUsername
        every { userRepository.findByUsername(any()) } returns internalUserProjection
        every { internalUserProjection.id } returns expectedInternalUserId

        // when
        val result = userService.determineUserId(usernamePasswordAuthentication)

        // then
        verify(exactly = 1) { userRepository.findByUsername(username = expectedUsername) }

        assertThat(result)
            .isEqualTo(expectedInternalUserId)
    }

    @Test
    fun `should throw an exception for an unrecognized UsernamePassword authentication token`() {
        // given
        val expectedUsername = "username"
        val expectedExceptionParams = arrayOf("User", "username=${expectedUsername}")

        every { usernamePasswordAuthentication.name } returns expectedUsername
        every { userRepository.findByUsername(any()) } returns null

        // when
        val result = catchThrowable { userService.determineUserId(usernamePasswordAuthentication) }

        // then
        verify(exactly = 1) { userRepository.findByUsername(username = expectedUsername) }

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    @Test
    fun `should throw IllegalArgumentException determining user id for an unsupported authentication token type`() {
        // given
        val unknownAuthentication = mockk<Authentication>()

        // when
        val exception = catchThrowable { userService.determineUserId(unknownAuthentication) }

        // then
        assertThat(exception)
            .isNotNull
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(unknownAuthentication::class.simpleName)
    }

    @Test
    fun `should return an expected internal user for a recognized OAuth2 authentication token`() {
        // given
        val expectedExternalId = "external-id"
        val expectedExternalProvider = "external-provider"

        every { oAuth2Authentication.name } returns expectedExternalId
        every { oAuth2Authentication.authorizedClientRegistrationId } returns expectedExternalProvider
        every { userRepository.findByExternalIdAndProvider(any(), any()) } returns internalUserProjection
        every { userMapper.mapToResponse(any<CompactInternalUserProjection>()) } returns userResponseDTO

        // when
        val result = userService.determineUser(oAuth2Authentication)

        // then
        verify(exactly = 1) { userRepository.findByExternalIdAndProvider(expectedExternalId, expectedExternalProvider) }
        verify(exactly = 1) { userMapper.mapToResponse(internalUserProjection) }

        assertThat(result)
            .isEqualTo(userResponseDTO)
    }

    @Test
    fun `should throw an exception determining user for an unrecognized OAuth2 authentication token`() {
        // given
        val expectedExternalId = "external-id"
        val expectedExternalProvider = "external-provider"
        val expectedExceptionParams = arrayOf(
            "User",
            "externalId=${expectedExternalId}, externalProvider=${expectedExternalProvider}"
        )

        every { oAuth2Authentication.name } returns expectedExternalId
        every { oAuth2Authentication.authorizedClientRegistrationId } returns expectedExternalProvider
        every { userRepository.findByExternalIdAndProvider(any(), any()) } returns null

        // when
        val result = catchThrowable { userService.determineUser(oAuth2Authentication) }

        // then
        verify(exactly = 1) { userRepository.findByExternalIdAndProvider(expectedExternalId, expectedExternalProvider) }
        verify { userMapper wasNot called }

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    @Test
    fun `should return an expected internal user for a recognized UsernamePassword authentication token`() {
        // given
        val expectedUsername = "username"

        every { usernamePasswordAuthentication.name } returns expectedUsername
        every { userRepository.findByUsername(any()) } returns internalUserProjection
        every { userMapper.mapToResponse(any<CompactInternalUserProjection>()) } returns userResponseDTO

        // when
        val result = userService.determineUser(usernamePasswordAuthentication)

        // then
        verify(exactly = 1) { userRepository.findByUsername(expectedUsername) }
        verify(exactly = 1) { userMapper.mapToResponse(internalUserProjection) }

        assertThat(result)
            .isEqualTo(userResponseDTO)
    }

    @Test
    fun `should throw an exception determining user for an unrecognized UsernamePassword authentication token`() {
        // given
        val expectedUsername = "username"
        val expectedExceptionParams = arrayOf("User", "username=${expectedUsername}")

        every { usernamePasswordAuthentication.name } returns expectedUsername
        every { userRepository.findByUsername(any()) } returns null

        // when
        val result = catchThrowable { userService.determineUser(usernamePasswordAuthentication) }

        // then
        verify(exactly = 1) { userRepository.findByUsername(username = expectedUsername) }
        verify(exactly = 1) { userMapper wasNot called }

        assertThat(result)
            .isInstanceOf(EntityNotFoundException::class.java)
            .extracting { it as EntityNotFoundException }
            .returns(expectedExceptionParams, from { it.params })
    }

    @Test
    fun `should throw IllegalArgumentException determining user for an unsupported authentication token type`() {
        // given
        val unknownAuthentication = mockk<Authentication>()

        // when
        val exception = catchThrowable { userService.determineUser(unknownAuthentication) }

        // then
        assertThat(exception)
            .isNotNull
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining(unknownAuthentication::class.simpleName)
    }
}
