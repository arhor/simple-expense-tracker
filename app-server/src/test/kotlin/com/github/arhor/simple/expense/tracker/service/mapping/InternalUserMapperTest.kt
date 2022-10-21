package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.function.Consumer

internal class InternalUserMapperTest : MapperTestBase() {

    @Autowired
    private lateinit var userMapper: InternalUserMapper

    @Test
    fun `should correctly map user entity to response dto`() {
        // given
        val user = InternalUser(
            id = Long.MAX_VALUE,
            username = "test-username",
            password = "test-password",
            currency = "test-currency",
        )

        // when
        val result = userMapper.mapToResponse(user)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.id)
                        .describedAs("id")
                        .isEqualTo(user.id)
                },
                {
                    assertThat(it.username)
                        .describedAs("username")
                        .isEqualTo(user.username)
                },
                {
                    assertThat(it.currency)
                        .describedAs("currency")
                        .isEqualTo(user.currency)
                })
    }

    @Test
    fun `should correctly map user compact projection to response dto`() {
        // given
        val id = Long.MAX_VALUE
        val username = "test-username"
        val currency = "test-currency"

        val user = CompactInternalUserProjection(id, username, currency)

        // when
        val result = userMapper.mapToResponse(user)

        // then
        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.id)
                        .describedAs("id")
                        .isEqualTo(id)
                },
                {
                    assertThat(it.username)
                        .describedAs("username")
                        .isEqualTo(username)
                },
                {
                    assertThat(it.currency)
                        .describedAs("currency")
                        .isEqualTo(currency)
                }
            )
    }

    @Test
    fun `should correctly map user request dto to entity also encoding password`() {
        // given
        val username = "test-username"
        val password = "test-password"
        val currency = "test-currency"

        val request = UserRequestDTO(username, password, currency)

        val encodedPassword = "encoded-test-password"

        every { passwordEncoder.encode(any()) } returns encodedPassword

        // when
        val result = userMapper.mapToUser(request)

        // then
        verify(exactly = 1) { passwordEncoder.encode(request.password) }

        assertThat(result)
            .isNotNull
            .satisfies(
                Consumer {
                    assertThat(it.username)
                        .describedAs("username")
                        .isEqualTo(username)
                },
                {
                    assertThat(it.password)
                        .describedAs("password")
                        .isEqualTo(encodedPassword)
                },
                {
                    assertThat(it.currency)
                        .describedAs("currency")
                        .isEqualTo(currency)
                }
            )
    }
}
