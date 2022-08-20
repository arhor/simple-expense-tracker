package com.github.arhor.simple.expense.tracker.util;

import lombok.val;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilsTest {

    @Nested
    class zoneIdOrDefaultUTC {
        @Test
        void should_return_expected_zone_id_for_a_non_null_input() {
            // given
            val timezone = TimeZone.getTimeZone("GMT");
            val expected = timezone.toZoneId();

            // when
            val result = TimeUtils.zoneIdOrDefaultUTC(timezone);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualTo(expected);
        }

        @Test
        void should_return_UTC_zone_id_for_a_null_input() {
            // given
            val expected = ZoneOffset.UTC;

            // when
            val result = TimeUtils.zoneIdOrDefaultUTC(null);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualTo(expected);
        }
    }

    @Nested
    class currentZonedDateTime {
        @Test
        void should_return_current_zoned_date_time_with_provided_timezone_accurate_to_milliseconds() {
            // given
            val timezone = TimeZone.getTimeZone("PST");
            val expected = ZonedDateTime.now(timezone.toZoneId());

            // when
            val result = TimeUtils.currentZonedDateTime(timezone);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualToIgnoringNanos(expected);
        }

        @Test
        void should_return_current_zoned_date_time_with_UTC_timezone_accurate_to_milliseconds() {
            // given
            val timezone = TimeZone.getTimeZone("UTC");
            val expected = ZonedDateTime.now(timezone.toZoneId());

            // when
            val result = TimeUtils.currentZonedDateTime(null);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualToIgnoringNanos(expected);
        }
    }

    @Nested
    class currentLocalDateTime {
        @Test
        void should_return_current_local_date_time_with_provided_timezone_accurate_to_milliseconds() {
            // given
            val timezone = TimeZone.getTimeZone("PST");
            val expected = LocalDateTime.now(timezone.toZoneId());

            // when
            val result = TimeUtils.currentLocalDateTime(timezone);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualToIgnoringNanos(expected);
        }

        @Test
        void should_return_current_local_date_time_with_UTC_timezone_accurate_to_milliseconds() {
            // given
            val timezone = TimeZone.getTimeZone("UTC");
            val expected = LocalDateTime.now(timezone.toZoneId());

            // when
            val result = TimeUtils.currentLocalDateTime(null);

            // then
            assertThat(result)
                .isNotNull()
                .isEqualToIgnoringNanos(expected);
        }
    }

    @Nested
    class createDateRange {
        @Test
        void should_return_current_month_date_range_with_provided_timezone_and_start_date_null_end_date_null() {
            // given
            val timezone = TimeZone.getTimeZone("PST");
            val today = LocalDate.now(timezone.toZoneId());
            val expectedDateRangeStart = today.with(firstDayOfMonth());
            val expectedDateRangeEnd = today.with(lastDayOfMonth());

            // when
            val result = TimeUtils.createDateRange(null, null, timezone);

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
            val timezone = TimeZone.getTimeZone("PST");

            val startDate = LocalDate.of(2022, 7, 15);
            val expectedDateRangeEnd = LocalDate.of(2022, 7, 31);

            // when
            val result = TimeUtils.createDateRange(startDate, null, timezone);

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
            val timezone = TimeZone.getTimeZone("PST");
            val startDate = LocalDate.of(2022, 7, 1);
            val endDate = LocalDate.of(2022, 9, 30);

            // when
            val result = TimeUtils.createDateRange(startDate, endDate, timezone);

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
            val today = LocalDate.now(ZoneOffset.UTC);
            val firstDayOfCurrentMonth = today.with(firstDayOfMonth());
            val lastDayOfCurrentMonth = today.with(lastDayOfMonth());

            // when
            val result = TimeUtils.createDateRange(null, null, null);

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
}
