package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    public ZonedDateTime now() {
        return currentDateTime(DEFAULT_TIME_ZONE);
    }

    @Override
    public ZonedDateTime now(final TimeZone timezone) {
        return currentDateTime(
            (timezone != null)
                ? timezone.toZoneId()
                : DEFAULT_TIME_ZONE
        );
    }

    @Override
    public ZonedDateTime weekAgo() {
        return now().minus(Duration.ofDays(7));
    }

    @Override
    public TemporalRange<LocalDate> convertToDateRange(
        final LocalDate startDate,
        final LocalDate endDate,
        final TimeZone timezone
    ) {
        var startDatePresent = startDate != null;
        var endDatePresent = endDate != null;

        var zoneId = (timezone != null)
            ? timezone.toZoneId()
            : DEFAULT_TIME_ZONE;

        var safeStartDate = startDatePresent
            ? startDate
            : (endDatePresent ? endDate : LocalDate.now(zoneId)).with(firstDayOfMonth());
        var safeEndDate = endDatePresent
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

    private ZonedDateTime currentDateTime(final ZoneId zoneId) {
        return ZonedDateTime.now(zoneId).truncatedTo(ChronoUnit.MILLIS);
    }
}
