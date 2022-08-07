package com.github.arhor.simple.expense.tracker.service;

import lombok.val;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.arhor.simple.expense.tracker.service.impl.StringSanitizerImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(StringSanitizerImpl.class)
public class StringSanitizerTest {

    @Autowired
    private StringSanitizer sanitizer;

    @Test
    void sanitize_should_return_safe_string_for_an_input_containing_script_injection() {
        // given
        val initialUserInput = "<script>alert('Hacked!');</script>";

        // when
        val sanitizedUserInput = sanitizer.sanitize(initialUserInput);

        // then
        assertThat(sanitizedUserInput)
            .isNotNull()
            .isNotBlank()
            .isNotEqualTo(initialUserInput)
            .doesNotContain("<", ">");
    }

    @Test
    void sanitize_should_return_the_same_string_for_an_input_without_unsafe_elements() {
        // given
        val initialUserInput = "Just simple text without unsafe elements";

        // when
        val sanitizedUserInput = sanitizer.sanitize(initialUserInput);

        // then
        assertThat(sanitizedUserInput)
            .isNotNull()
            .isNotBlank()
            .isEqualTo(initialUserInput);
    }
}
