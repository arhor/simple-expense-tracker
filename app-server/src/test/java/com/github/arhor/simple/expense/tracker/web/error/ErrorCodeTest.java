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

    @EnumSource(ErrorCode.class)
    @ParameterizedTest(name = "should have localization for {arguments} error code")
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

    @EnumSource(ErrorCode.Type.class)
    @ParameterizedTest(name = "should not have duplicate numeric values within {arguments} type")
    void each_error_code_type_should_not_have_numeric_value_duplicates(final ErrorCode.Type type) {
        // when
        var errorCodesByType = stream(ErrorCode.values()).filter(it -> it.getType() == type).toList();

        // then
        assertThat(errorCodesByType)
            .extracting(ErrorCode::getNumericValue)
            .doesNotHaveDuplicates();
    }
}
