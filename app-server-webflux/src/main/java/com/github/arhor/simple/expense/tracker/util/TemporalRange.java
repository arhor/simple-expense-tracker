package com.github.arhor.simple.expense.tracker.util;

import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * Represent temporal range with given start and end.
 *
 * @param start start of the temporal range
 * @param end   end of the temporal range
 * @param <T>   temporal type
 *
 * @see Record
 * @see Temporal
 */
public record TemporalRange<T extends Temporal>(
    T start,
    T end
) {

    public TemporalRange {
        Objects.requireNonNull(start, "TemporalRange start cannot be null");
        Objects.requireNonNull(end, "TemporalRange end cannot be null");
    }
}
