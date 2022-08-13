package com.github.arhor.simple.expense.tracker.util;

import lombok.SneakyThrows;
import lombok.val;

import java.util.function.Consumer;
import java.util.function.Function;

public final class JavaLangExt {

    private JavaLangExt() { /* should not be instantiated */ }

    @SneakyThrows
    public static <T extends AutoCloseable, R> R use(final T self, final Function<T, R> action) {
        try (val s = self) {
            return action.apply(s);
        }
    }

    @SneakyThrows
    public static <T extends AutoCloseable> void use(final T self, final Consumer<T> action) {
        try (val s = self) {
            action.accept(s);
        }
    }
}
