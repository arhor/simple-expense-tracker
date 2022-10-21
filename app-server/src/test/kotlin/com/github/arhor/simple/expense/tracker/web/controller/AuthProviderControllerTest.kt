package com.github.arhor.simple.expense.tracker.web.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

@WebMvcTest(AuthProviderController::class)
internal class AuthProviderControllerTest : BaseControllerTest() {

    @Test
    fun `should pass`() {
        // given
        val authProvidersEndPoint = applicationProps.apiUrlPath("/auth-providers")
        val authRequestBaseUri = applicationProps.authRequestBaseUri
        val expectedResponseBody = /* language=JSON */ """
            [
              { "name": "github", "href": "$authRequestBaseUri/github" },
              { "name": "google", "href": "$authRequestBaseUri/google" }
            ]
        """.trimIndent()

        // when
        val awaitResult = http.get(authProvidersEndPoint)

        // then
        awaitResult.andExpect {
            status { isOk() }
            content {
                contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                json(expectedResponseBody)
            }
        }
    }
}
