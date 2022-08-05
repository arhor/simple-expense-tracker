package com.github.arhor.simple.expense.tracker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilsTest {

    @Test
    void should_return_expected_zone_id_for_a_non_null_input() {
        // given
        var timezone = TimeZone.getTimeZone("GMT");
        var expected = timezone.toZoneId();

        // when
        var result = TimeUtils.zoneIdOrDefaultUTC(timezone);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    void should_return_UTC_zone_id_for_a_null_input() {
        // given
        var expected = ZoneOffset.UTC;

        // when
        var result = TimeUtils.zoneIdOrDefaultUTC(null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualTo(expected);
    }

    @Test
    void should_return_current_zoned_date_time_with_provided_timezone_accurate_to_milliseconds() {
        // given
        var timezone = TimeZone.getTimeZone("PST");
        var expected = ZonedDateTime.now(timezone.toZoneId());

        // when
        var result = TimeUtils.currentZonedDateTime(timezone);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualToIgnoringNanos(expected);
    }

    @Test
    void should_return_current_zoned_date_time_with_UTC_timezone_accurate_to_milliseconds() {
        // given
        var timezone = TimeZone.getTimeZone("UTC");
        var expected = ZonedDateTime.now(timezone.toZoneId());

        // when
        var result = TimeUtils.currentZonedDateTime(null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualToIgnoringNanos(expected);
    }

    @Test
    void should_return_current_local_date_time_with_provided_timezone_accurate_to_milliseconds() {
        // given
        var timezone = TimeZone.getTimeZone("PST");
        var expected = LocalDateTime.now(timezone.toZoneId());

        // when
        var result = TimeUtils.currentLocalDateTime(timezone);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualToIgnoringNanos(expected);
    }

    @Test
    void should_return_current_local_date_time_with_UTC_timezone_accurate_to_milliseconds() {
        // given
        var timezone = TimeZone.getTimeZone("UTC");
        var expected = LocalDateTime.now(timezone.toZoneId());

        // when
        var result = TimeUtils.currentLocalDateTime(null);

        // then
        assertThat(result)
            .isNotNull()
            .isEqualToIgnoringNanos(expected);
    }

    @Test
    void should_return_current_month_date_range_with_provided_timezone_and_start_date_null_end_date_null() {
        // given
        var timezone = TimeZone.getTimeZone("PST");
        var today = LocalDate.now(timezone.toZoneId());
        var expectedDateRangeStart = today.with(firstDayOfMonth());
        var expectedDateRangeEnd = today.with(lastDayOfMonth());

        // when
        var result = TimeUtils.createDateRange(null, null, timezone);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .as("date range start")
                        .isNotNull()
                        .isEqualTo(expectedDateRangeStart);
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .as("date range end")
                        .isNotNull()
                        .isEqualTo(expectedDateRangeEnd);
                }
            );
    }

    @Test
    void should_return_current_month_date_range_with_provided_timezone_and_start_date_provided_end_date_null() {
        // given
        var timezone = TimeZone.getTimeZone("PST");

        var startDate = LocalDate.of(2022, 7, 15);
        var expectedDateRangeEnd = LocalDate.of(2022, 7, 31);

        // when
        var result = TimeUtils.createDateRange(startDate, null, timezone);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .as("date range start")
                        .isNotNull()
                        .isEqualTo(startDate);
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .as("date range end")
                        .isNotNull()
                        .isEqualTo(expectedDateRangeEnd);
                }
            );
    }

    @Test
    void should_return_current_month_date_range_with_provided_timezone_start_date_and_end_date() {
        // given
        var timezone = TimeZone.getTimeZone("PST");
        var startDate = LocalDate.of(2022, 7, 1);
        var endDate = LocalDate.of(2022, 9, 30);

        // when
        var result = TimeUtils.createDateRange(startDate, endDate, timezone);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .as("date range start")
                        .isNotNull()
                        .isEqualTo(startDate);
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .as("date range end")
                        .isNotNull()
                        .isEqualTo(endDate);
                }
            );
    }

    @Test
    void should_return_current_month_date_range_with_UTC_timezone_for_all_nulls_input() {
        // given
        var today = LocalDate.now(ZoneOffset.UTC);
        var firstDayOfCurrentMonth = today.with(firstDayOfMonth());
        var lastDayOfCurrentMonth = today.with(lastDayOfMonth());

        // when
        var result = TimeUtils.createDateRange(null, null, null);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .as("date range start")
                        .isNotNull()
                        .isEqualTo(firstDayOfCurrentMonth);
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .as("date range end")
                        .isNotNull()
                        .isEqualTo(lastDayOfCurrentMonth);
                }
            );
    }
}
