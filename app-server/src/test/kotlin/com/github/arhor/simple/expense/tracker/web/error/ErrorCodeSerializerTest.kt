package com.github.arhor.simple.expense.tracker.web.error

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ErrorCodeSerializerTest {

    private val errorCodeSerializer = ErrorCodeSerializer()

    private val generator = mockk<JsonGenerator>()
    private val provider = mockk<SerializerProvider>()
    private val serializedValue = slot<String>()

    @BeforeEach
    fun setUp() {
        serializedValue.clear()
        every { generator.writeString(any<String>()) } just runs
    }

    @ParameterizedTest
    @EnumSource(ErrorCode::class)
    fun `each error code should be correctly serialized to string`(
        // given
        errorCode: ErrorCode
    ) {
        // when
        errorCodeSerializer.serialize(errorCode, generator, provider)

        // then
        verify(exactly = 1) { generator.writeString(capture(serializedValue)) }

        assertThat(serializedValue.captured)
            .isNotBlank
            .contains(
                errorCode.type.toString(),
                errorCode.value.toString(),
            )
    }
}
