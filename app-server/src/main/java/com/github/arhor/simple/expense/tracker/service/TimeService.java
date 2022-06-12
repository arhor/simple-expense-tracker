package com.github.arhor.simple.expense.tracker.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.TimeZone;

public interface TimeService {

    Instant now();

    Instant now(TimeZone timezone);

    Instant weekAgo();

    TemporalRange<LocalDate> convertToDateRange(LocalDate startDate, LocalDate endDate, TimeZone timezone);

    record TemporalRange<T extends Temporal>(T start, T end) {
    }
}
