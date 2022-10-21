package com.github.arhor.simple.expense.tracker.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.util.TimeZone
import java.util.function.Consumer

@Suppress("ClassName")
internal class TimeUtilsTest {

    @Nested
    inner class `TimeUtils # zoneIdOrDefaultUTC` {
        @Test
        fun `should return expected zone id for a non null input`() {
            // given
            val timezone = TimeZone.getTimeZone("GMT")
            val expected = timezone.toZoneId()

            // when
            val result = zoneIdOrDefaultUTC(timezone)

            // then
            assertThat(result)
                .isNotNull
                .isEqualTo(expected)
        }

        @Test
        fun `should return UTC zone id for a null input`() {
            // given
            val expected = ZoneOffset.UTC

            // when
            val result = zoneIdOrDefaultUTC(null)

            // then
            assertThat(result)
                .isNotNull
                .isEqualTo(expected)
        }
    }

    @Nested
    inner class `TimeUtils # currentZonedDateTime` {
        @Test
        fun `should return current zoned date time with provided timezone accurate to milliseconds`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")
            val expected = ZonedDateTime.now(timezone.toZoneId())

            // when
            val result = currentZonedDateTime(timezone)

            // then
            assertThat(result)
                .isNotNull
                .isEqualToIgnoringNanos(expected)
        }

        @Test
        fun `should return current zoned date time with UTC timezone accurate to milliseconds`() {
            // given
            val timezone = TimeZone.getTimeZone("UTC")
            val expected = ZonedDateTime.now(timezone.toZoneId())

            // when
            val result = currentZonedDateTime(null)

            // then
            assertThat(result)
                .isNotNull
                .isEqualToIgnoringNanos(expected)
        }
    }

    @Nested
    inner class `TimeUtils # currentLocalDateTime` {
        @Test
        fun `should return current local date time with provided timezone accurate to milliseconds`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")
            val expected = LocalDateTime.now(timezone.toZoneId())

            // when
            val result = currentLocalDateTime(timezone)

            // then
            assertThat(result)
                .isNotNull
                .isEqualToIgnoringNanos(expected)
        }

        @Test
        fun `should return current local date time with UTC timezone accurate to milliseconds`() {
            // given
            val timezone = TimeZone.getTimeZone("UTC")
            val expected = LocalDateTime.now(timezone.toZoneId())

            // when
            val result = currentLocalDateTime(null)

            // then
            assertThat(result)
                .isNotNull
                .isEqualToIgnoringNanos(expected)
        }
    }

    @Nested
    inner class `TimeUtils # createDateRange` {
        @Test
        fun `should return current month date range with provided timezone and start date null end date null`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")
            val today = LocalDate.now(timezone.toZoneId())
            val expectedDateRangeStart = today.with(firstDayOfMonth())
            val expectedDateRangeEnd = today.with(lastDayOfMonth())

            // when
            val result = createDateRange(null, null, timezone)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("date range start")
                            .isNotNull
                            .isEqualTo(expectedDateRangeStart)
                    },
                    {
                        assertThat(it.end)
                            .describedAs("date range end")
                            .isNotNull
                            .isEqualTo(expectedDateRangeEnd)
                    }
                )
        }

        @Test
        fun `should return current month date range with provided timezone and start date provided end date null`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")

            val startDate = LocalDate.of(2022, 7, 15)
            val expectedDateRangeEnd = LocalDate.of(2022, 7, 31)

            // when
            val result = createDateRange(startDate, null, timezone)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("date range start")
                            .isNotNull
                            .isEqualTo(startDate)
                    },
                    {
                        assertThat(it.end)
                            .describedAs("date range end")
                            .isNotNull
                            .isEqualTo(expectedDateRangeEnd)
                    }
                )
        }

        @Test
        fun `should return current month date range with provided timezone start date and end date`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")
            val startDate = LocalDate.of(2022, 7, 1)
            val endDate = LocalDate.of(2022, 9, 30)

            // when
            val result = createDateRange(startDate, endDate, timezone)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("date range start")
                            .isNotNull
                            .isEqualTo(startDate)
                    },
                    {
                        assertThat(it.end)
                            .describedAs("date range end")
                            .isNotNull
                            .isEqualTo(endDate)
                    }
                )
        }

        @Test
        fun `should return current month date range with UTC timezone for all nulls input`() {
            // given
            val today = LocalDate.now(ZoneOffset.UTC)
            val firstDayOfCurrentMonth = today.with(firstDayOfMonth())
            val lastDayOfCurrentMonth = today.with(lastDayOfMonth())

            // when
            val result = createDateRange(null, null, null)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("date range start")
                            .isNotNull
                            .isEqualTo(firstDayOfCurrentMonth)
                    },
                    {
                        assertThat(it.end)
                            .describedAs("date range end")
                            .isNotNull
                            .isEqualTo(lastDayOfCurrentMonth)
                    }
                )
        }
    }
}
