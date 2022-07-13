package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.TimeZone;

public interface TimeService {

    ZonedDateTime now();

    ZonedDateTime now(TimeZone timezone);

    ZonedDateTime weekAgo();

    TemporalRange<LocalDate> convertToDateRange(LocalDate startDate, LocalDate endDate, TimeZone timezone);

    record TemporalRange<T extends Temporal>(T start, T end) {
    }
}
