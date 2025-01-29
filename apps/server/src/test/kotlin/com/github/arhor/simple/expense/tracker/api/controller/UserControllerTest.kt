package com.github.arhor.simple.expense.tracker.api.controller

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.UserService
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(UserController::class)
internal class UserControllerTest : ControllerTestSupport() {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `should return status 201, user info and location header creating new user`() {
        // given
        val user = slot<UserRequestDTO>()
        val expectedId = TestUser.id
        val expectedUsername = TestUser.username
        val expectedPassword = TestUser.password
        val expectedCurrency = TestUser.currency

        every { userService.createNewUser(request = any()) } returns UserResponseDTO(
            expectedId,
            expectedUsername,
            expectedCurrency
        )

        // when
        val awaitResult = http.post(appProps.apiUrlPath("/users")) {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                    "username": "$expectedUsername",
                    "password": "$expectedPassword",
                    "currency": "$expectedCurrency"
                }
            """.trimIndent()
        }

        // then
        verify(exactly = 1) { userService.createNewUser(request = capture(user)) }

        assertThat(user.captured)
            .returns(expectedUsername, from { it.username })
            .returns(expectedPassword, from { it.password })
            .returns(expectedCurrency, from { it.currency })

        awaitResult.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(expectedId) }
            jsonPath("$.username") { value(expectedUsername) }
            jsonPath("$.currency") { value(expectedCurrency) }
        }
    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    fun `should return status 200 and expected info for authenticated user`() {
        // given
        val expectedId = TestUser.id
        val expectedUsername = TestUser.username
        val expectedCurrency = TestUser.currency

        // when
        val result = http.get(appProps.apiUrlPath("/users/current"))

        // then
        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(expectedId) }
            jsonPath("$.username") { value(expectedUsername) }
            jsonPath("$.currency") { value(expectedCurrency) }
        }
    }
}
