package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(AuthProviderController::class)
internal class AuthProviderControllerTest : BaseControllerTest() {

    @Test
    fun `should return an expected list of authentication providers`() {
        // given
        val authProvidersEndPoint = applicationProps.apiUrlPath("/auth-providers")
        val authRequestBaseUri = applicationProps.authRequestBaseUri

        every { authService.getAvailableProviders() } answers {
            listOf("github", "google").map {
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
                    /* language=JSON */
                    """
                        [
                          { "name": "github", "href": "$authRequestBaseUri/github" },
                          { "name": "google", "href": "$authRequestBaseUri/google" }
                        ]
                    """.trimIndent()
                )
            }
        }
    }
}
