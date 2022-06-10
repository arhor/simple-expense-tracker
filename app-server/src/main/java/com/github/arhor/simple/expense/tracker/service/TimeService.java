package com.github.arhor.simple.expense.tracker.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.TimeZone;

public interface TimeService {

    Instant now();

    Instant now(TimeZone timeZone);

    Instant weekAgo();

    TemporalRange<LocalDateTime> convertToSystemTimeRange(OffsetDateTime startDate, OffsetDateTime endDate);

    record TemporalRange<T extends Temporal>(T start, T end) {
    }
}
