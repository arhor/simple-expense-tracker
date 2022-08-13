package com.github.arhor.simple.expense.tracker.util;

import java.util.Collections;
import java.util.Map;

public final class CollectionExt {

    private CollectionExt() { /* should not be instantiated */ }

    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> self) {
        return self != null ? self : Collections.emptyMap();
    }
}
