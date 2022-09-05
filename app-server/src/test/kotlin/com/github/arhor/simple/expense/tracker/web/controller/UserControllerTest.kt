package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(UserController::class)
internal class UserControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var applicationProps: ApplicationProps

    @MockkBean
    private lateinit var userService: UserService

    private val auth = slot<Authentication>()
    private val user = slot<UserRequestDTO>()

    @Test
    fun `should return status 201 user info and location header creating new user`() {
        // given
        val usersEndPoint = applicationProps.apiUrlPath("/users")

        val expectedId = 1L
        val expectedUsername = "Username"
        val expectedPassword = "Password123"
        val expectedCurrency = "USD"

        every { userService.createNewUser(any()) } returns UserResponseDTO().apply {
            id = expectedId
            username = expectedUsername
            currency = expectedCurrency
        }

        // when
        val result = http.post(usersEndPoint) {
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
        verify(exactly = 1) { userService.createNewUser(capture(user)) }

        assertThat(user.captured)
            .returns(expectedUsername, from { it.username })
            .returns(expectedPassword, from { it.password })
            .returns(expectedCurrency, from { it.currency.get() })

        result.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(expectedId) }
            jsonPath("$.username") { value(expectedUsername) }
            jsonPath("$.currency") { value(expectedCurrency) }
        }
    }

    @Test
    @WithMockUser
    fun `should return status 200 and expected info for authenticated user`() {
        // given
        val expectedId = 1L
        val expectedUsername = "Username"
        val expectedCurrency = "USD"

        every { userService.determineUser(any()) } returns UserResponseDTO().apply {
            id = expectedId
            username = expectedUsername
            currency = expectedCurrency
        }

        // when
        val result = http.get(applicationProps.apiUrlPath("/users/current")) {
            param("current", "true")
        }

        // then
        verify(exactly = 1) { userService.determineUser(capture(auth)) }

        assertThat(auth.captured)
            .isNotNull
            .satisfies(authenticatedUser)

        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(expectedId) }
            jsonPath("$.username") { value(expectedUsername) }
            jsonPath("$.currency") { value(expectedCurrency) }
        }
    }
}
