package com.github.arhor.simple.expense.tracker.util;

import java.time.temporal.Temporal

/**
 * Represent temporal range with given start and end.
 *
 * @param start start of the temporal range
 * @param end   end of the temporal range
 * @param <T>   temporal type
 *
 * @see java.time.temporal.Temporal
 */
data class TemporalRange<T : Temporal>(
    val start: T,
    val end: T,
)
