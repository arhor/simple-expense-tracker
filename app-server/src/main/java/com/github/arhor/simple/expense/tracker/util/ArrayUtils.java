package com.github.arhor.simple.expense.tracker.util;

public final class ArrayUtils {

    private ArrayUtils() { /* should not be instantiated */ }

    @SafeVarargs
    public static <T> T[] array(T... values) {
        return values;
    }
}
