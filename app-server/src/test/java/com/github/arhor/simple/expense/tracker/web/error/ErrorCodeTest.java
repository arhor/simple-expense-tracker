package com.github.arhor.simple.expense.tracker.web.error;

import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.arhor.simple.expense.tracker.config.LocalizationConfig;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(LocalizationConfig.class)
class ErrorCodeTest {

    @Autowired
    private MessageSource messages;

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    void each_error_code_label_should_be_translated(final ErrorCode code) {
        // given
        var label = code.getLabel();
        var locale = Locale.ENGLISH;

        // when
        var message = messages.getMessage(label, null, locale);

        // then
        assertThat(message)
            .isNotBlank();
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    void each_error_code_numeric_value_length_should_be_less_than_or_equal_to_maximum(final ErrorCode code) {
        // given
        var value = code.getNumericValue();
        var limit = ErrorCodeSerializer.CODE_MAX_LENGTH;

        // when
        var string = String.valueOf(value);

        // then
        assertThat(string)
            .hasSizeLessThanOrEqualTo(limit);
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.Type.class)
    void each_error_code_type_should_not_have_numeric_value_duplicates(final ErrorCode.Type type) {
        // when
        var errorCodesByType = stream(ErrorCode.values()).filter(it -> it.getType() == type).toList();

        // then
        assertThat(errorCodesByType)
            .extracting(ErrorCode::getNumericValue)
            .doesNotHaveDuplicates();
    }
}
