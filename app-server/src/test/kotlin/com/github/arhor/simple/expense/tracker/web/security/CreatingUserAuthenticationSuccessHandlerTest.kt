package com.github.arhor.simple.expense.tracker.web.security

import com.github.arhor.simple.expense.tracker.service.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockKExtension::class)
internal class CreatingUserAuthenticationSuccessHandlerTest {

    @MockK
    private lateinit var userService: UserService

    @MockK
    private lateinit var authentication: OAuth2AuthenticationToken

    @RelaxedMockK
    private lateinit var request: HttpServletRequest

    @RelaxedMockK
    private lateinit var response: HttpServletResponse

    @InjectMockKs
    private lateinit var authenticationSuccessHandler: CreatingUserAuthenticationSuccessHandler

    @Test
    fun `should call UserService # createNewUserIfNecessary on authentication success`() {
        // given
        every { userService.createNewUserIfNecessary(any()) } just runs
        every { request.getSession(any()) } answers { callOriginal() }

        // when
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)

        // then
        verify(exactly = 1) { userService.createNewUserIfNecessary(auth = authentication) }
    }
}
