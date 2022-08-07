package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.val;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.service.StringSanitizer;


@Service
public class StringSanitizerImpl implements StringSanitizer {

    private static final Map<? extends String, ? extends String> MAPPINGS = Map.of(
        "<", "&lt;",
        ">", "&gt;",
        "&", "&amp;",
        "\"", "&quot;"
    );

    @Override
    public String sanitize(final String input) {
        if (input != null) {
            var result = input;
            for (val entry : MAPPINGS.entrySet()) {
                val unsafeElement = entry.getKey();
                val replacement = entry.getValue();

                result = result.replace(unsafeElement, replacement);
            }
            return result;
        }
        return null;
    }
}
