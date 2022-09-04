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
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.function.Consumer

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
        val expectedUsername = "username"
        val expectedPassword = "Password1"
        val expectedCurrency = "USD"

        val requestBody = """
            {
                "username": "$expectedUsername",
                "password": "$expectedPassword",
                "currency": "$expectedCurrency"
            }
            """.trimIndent()

        val response = UserResponseDTO().apply {
            id = expectedId
            username = expectedUsername
            currency = expectedCurrency
        }

        every { userService.createNewUser(any()) } returns response

        // when
        val result = http.post(usersEndPoint) {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }

        // then
        verify(exactly = 1) { userService.createNewUser(capture(user)) }

        assertThat(user.captured)
            .satisfies(
                Consumer {
                    assertThat(it.username)
                        .describedAs("username")
                        .isNotNull
                        .isEqualTo(expectedUsername)
                },
                {
                    assertThat(it.password)
                        .describedAs("password")
                        .isNotNull
                        .isEqualTo(expectedPassword)
                },
                {
                    assertThat(it.currency)
                        .describedAs("currency")
                        .isNotNull
                        .contains(expectedCurrency)
                }
            )

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
        val usersEndPoint = applicationProps.apiUrlPath("/users")

        val response = UserResponseDTO()
        response.id = 1L
        response.username = "user"
        response.currency = "USD"

        every { userService.determineUser(any()) } returns response

        // when
        val result = http.get(usersEndPoint) {
            param("current", "true")
        }

        // then
        verify(exactly = 1) { userService.determineUser(capture(auth)) }

        assertThat(auth.captured)
            .isNotNull
            .satisfies(Consumer { authenticatedUser(it) })

        result.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(response.id) }
            jsonPath("$.username") { value(response.username) }
            jsonPath("$.currency") { value(response.currency) }
        }
    }
}
