package com.github.arhor.simple.expense.tracker.web.error

import com.github.arhor.simple.expense.tracker.config.ConfigureLocalization
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.util.Locale

@SpringJUnitConfig(ConfigureLocalization::class)
internal class ErrorCodeTest {

    @Autowired
    private lateinit var messages: MessageSource

    @ParameterizedTest
    @EnumSource(ErrorCode::class)
    fun `each error code label should be translated`(code: ErrorCode) {
        // given
        val label = code.label
        val locale = Locale.ENGLISH

        // when
        val message = messages.getMessage(label, null, locale)

        // then
        assertThat(message)
            .isNotBlank
    }

    @ParameterizedTest
    @EnumSource(ErrorCode::class)
    fun `each error code numeric value length should be less than or equal to maximum`(code: ErrorCode) {
        // given
        val value = code.numericValue
        val limit = ErrorCodeSerializer.CODE_MAX_LENGTH

        // when
        val string = value.toString()

        // then
        assertThat(string)
            .hasSizeLessThanOrEqualTo(limit)
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.Type::class)
    fun `each error code type should not have numeric value duplicates`(type: ErrorCode.Type) {
        // when
        val errorCodesByType = ErrorCode.values().filter { it.type == type }

        // then
        assertThat(errorCodesByType)
            .extracting(ErrorCode::numericValue)
            .doesNotHaveDuplicates()
    }
}
