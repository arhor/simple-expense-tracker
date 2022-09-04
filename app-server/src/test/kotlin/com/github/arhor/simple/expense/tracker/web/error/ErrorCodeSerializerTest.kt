package com.github.arhor.simple.expense.tracker.web.error

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@ExtendWith(MockKExtension::class)
internal class ErrorCodeSerializerTest {

    private val errorCodeSerializer = ErrorCodeSerializer()

    @MockK
    private lateinit var generator: JsonGenerator

    @MockK
    private lateinit var provider: SerializerProvider

    @ParameterizedTest
    @EnumSource(ErrorCode::class)
    fun `each error code should be correctly serialized to string`(errorCode: ErrorCode) {
        // given
        val string = slot<String>()

        every { generator.writeString(any<String>()) } just runs

        // when
        errorCodeSerializer.serialize(errorCode, generator, provider)

        // then
        verify(exactly = 1) { generator.writeString(capture(string)) }

        assertThat(string.captured)
            .isNotBlank
            .contains(
                errorCode.type.toString(),
                errorCode.numericValue.toString(),
            )
    }
}
