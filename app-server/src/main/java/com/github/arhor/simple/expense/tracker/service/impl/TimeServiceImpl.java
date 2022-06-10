package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.service.TimeService;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class TimeServiceImpl implements TimeService {

    private static final ZoneOffset DEFAULT_TIME_ZONE = ZoneOffset.UTC;

    @Override
    public Instant now() {
        return currentInstant(DEFAULT_TIME_ZONE);
    }

    @Override
    public Instant now(final TimeZone timeZone) {
        return currentInstant(
            (timeZone != null)
                ? timeZone.toZoneId()
                : DEFAULT_TIME_ZONE
        );
    }

    @Override
    public Instant weekAgo() {
        return now().minus(Duration.ofDays(7));
    }

    @Override
    public TemporalRange<LocalDateTime> convertToSystemTimeRange(
        final OffsetDateTime startDate,
        final OffsetDateTime endDate
    ) {
        final var startDatePresent = startDate != null;
        final var endDatePresent = endDate != null;

        final var systemStartDate = toSystemLocalDateTime(
            startDatePresent
                ? startDate
                : (endDatePresent ? endDate : OffsetDateTime.now()).with(firstDayOfMonth())
        );
        final var systemEndDate = toSystemLocalDateTime(
            endDatePresent
                ? endDate
                : (startDatePresent ? startDate : OffsetDateTime.now()).with(lastDayOfMonth())
        );
        if (systemStartDate.isAfter(systemEndDate)) {
            throw new IllegalStateException(
                "Start date cannot be placed after end date - start: %s, end: %s".formatted(
                    systemStartDate,
                    systemEndDate
                )
            );
        }
        return new TemporalRange<>(systemStartDate, systemEndDate);
    }

    private LocalDateTime toSystemLocalDateTime(final OffsetDateTime offsetDateTime) {
        return offsetDateTime.withOffsetSameLocal(DEFAULT_TIME_ZONE).toLocalDateTime();
    }

    private Instant currentInstant(final ZoneId zoneId) {
        return Clock.system(zoneId).instant().truncatedTo(ChronoUnit.MILLIS);
    }
}
