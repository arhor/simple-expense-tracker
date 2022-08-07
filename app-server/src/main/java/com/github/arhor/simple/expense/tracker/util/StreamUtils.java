package com.github.arhor.simple.expense.tracker.util;

import lombok.val;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StreamUtils {

    private StreamUtils() { /* should not be instantiated */ }

    public static <T, R> R useStream(final Supplier<Stream<T>> source, final Function<Stream<T>, R> action) {
        return useStream(source.get(), action);
    }

    public static <T> void useStream(final Supplier<Stream<T>> source, final Consumer<Stream<T>> action) {
        useStream(source.get(), action);
    }

    public static <T, R> R useStream(final Stream<T> stream, final Function<Stream<T>, R> action) {
        try (val s = stream) {
            return action.apply(s);
        }
    }

    public static <T> void useStream(final Stream<T> stream, final Consumer<Stream<T>> action) {
        try (val s = stream) {
            action.accept(s);
        }
    }
}
