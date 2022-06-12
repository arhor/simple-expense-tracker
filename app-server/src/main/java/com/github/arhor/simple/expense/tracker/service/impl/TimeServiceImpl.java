package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
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
    public Instant now(final TimeZone timezone) {
        return currentInstant(
            (timezone != null)
                ? timezone.toZoneId()
                : DEFAULT_TIME_ZONE
        );
    }

    @Override
    public Instant weekAgo() {
        return now().minus(Duration.ofDays(7));
    }

    @Override
    public TemporalRange<LocalDate> convertToDateRange(
        final LocalDate startDate,
        final LocalDate endDate,
        final TimeZone timezone
    ) {
        final var startDatePresent = startDate != null;
        final var endDatePresent = endDate != null;

        final ZoneId zoneId = (timezone != null)
            ? timezone.toZoneId()
            : DEFAULT_TIME_ZONE;

        final var safeStartDate = startDatePresent
            ? startDate
            : (endDatePresent ? endDate : LocalDate.now(zoneId)).with(firstDayOfMonth());
        final var safeEndDate = endDatePresent
            ? endDate
            : (startDatePresent ? startDate : LocalDate.now(zoneId)).with(lastDayOfMonth());

        if (safeStartDate.isAfter(safeEndDate)) {
            throw new IllegalStateException(
                "Start date cannot be greater than/*- end date - start: %s, end: %s".formatted(
                    safeStartDate,
                    safeEndDate
                )
            );
        }
        return new TemporalRange<>(safeStartDate, safeEndDate);
    }

    private Instant currentInstant(final ZoneId zoneId) {
        return Clock.system(zoneId).instant().truncatedTo(ChronoUnit.MILLIS);
    }
}
