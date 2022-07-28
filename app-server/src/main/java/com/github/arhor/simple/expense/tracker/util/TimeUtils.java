package com.github.arhor.simple.expense.tracker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public final class TimeUtils {

    private TimeUtils() { /* should not be instantiated */ }

    public static ZoneId zoneIdOrDefaultUTC(final TimeZone timezone) {
        return (timezone != null) ? timezone.toZoneId() : ZoneOffset.UTC;
    }

    public static ZonedDateTime currentZonedDateTime() {
        return currentZonedDateTime(null);
    }

    public static ZonedDateTime currentZonedDateTime(final TimeZone timezone) {
        return ZonedDateTime.now(zoneIdOrDefaultUTC(timezone)).truncatedTo(ChronoUnit.MILLIS);
    }

    public static LocalDateTime currentLocalDateTime() {
        return currentLocalDateTime(null);
    }

    public static LocalDateTime currentLocalDateTime(final TimeZone timezone) {
        return LocalDateTime.now(zoneIdOrDefaultUTC(timezone)).truncatedTo(ChronoUnit.MILLIS);
    }

    public static TemporalRange<LocalDate> createDateRange(
        final LocalDate startDate,
        final LocalDate endDate,
        final TimeZone timezone
    ) {
        var startDatePresent = startDate != null;
        var endDatePresent = endDate != null;

        var zoneId = zoneIdOrDefaultUTC(timezone);

        var safeStartDate = startDatePresent
            ? startDate
            : (endDatePresent ? endDate : LocalDate.now(zoneId)).with(firstDayOfMonth());
        var safeEndDate = endDatePresent
            ? endDate
            : (startDatePresent ? startDate : LocalDate.now(zoneId)).with(lastDayOfMonth());

        if (safeStartDate.isAfter(safeEndDate)) {
            throw new IllegalStateException(
                "Required start date less than or equal end date, found: start = %s, end = %s".formatted(
                    safeStartDate,
                    safeEndDate
                )
            );
        }
        return new TemporalRange<>(safeStartDate, safeEndDate);
    }
}
