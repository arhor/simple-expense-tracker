package com.github.arhor.simple.expense.tracker.service.mapping;

import java.util.Optional;

public final class OptionalMapper {

    public static <T> Optional<T> wrap(T value) {
        return Optional.ofNullable(value);
    }

    public static <T> T unwrap(Optional<T> value) {
        return value.orElse(null);
    }
}
