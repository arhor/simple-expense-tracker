package com.github.arhor.simple.expense.tracker.api.controller

import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import com.github.arhor.simple.expense.tracker.service.AuthProviderService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(AuthProviderController::class)
internal class AuthProviderControllerTest : ControllerTestSupport() {

    @MockkBean
    private lateinit var authService: AuthProviderService

    @Test
    fun `should return an expected list of authentication providers`() {
        // given
        val authProvidersEndPoint = appProps.apiUrlPath("/auth-providers")
        val authRequestBaseUri = appProps.authRequestBaseUri

        every { authService.getAvailableProviders() } answers {
            listOf(GITHUB, GOOGLE).map {
                AuthProviderDTO(
                    it,
                    "$authRequestBaseUri/$it"
                )
            }
        }

        // when
        val awaitResult = http.get(authProvidersEndPoint)

        // then
        awaitResult.andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                json(
                    """
                        [
                          { "name": "$GITHUB", "href": "$authRequestBaseUri/$GITHUB" },
                          { "name": "$GOOGLE", "href": "$authRequestBaseUri/$GOOGLE" }
                        ]
                    """.trimIndent()
                )
            }
        }
    }

    companion object {
        private const val GITHUB = "github"
        private const val GOOGLE = "google"
    }
}
