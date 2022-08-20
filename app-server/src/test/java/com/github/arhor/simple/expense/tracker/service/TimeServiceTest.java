package com.github.arhor.simple.expense.tracker.service;

import lombok.val;

import java.time.LocalDate;
import java.util.TimeZone;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

@SpringJUnitConfig(TimeServiceImpl.class)
class TimeServiceTest {

    @Autowired
    private TimeService timeService;

    @Nested
    class now {
        @Test
        void should_return_non_null_date_time_with_default_timezone() {
            // when
            val result = timeService.now();

            // then
            assertThat(result)
                .isNotNull();
        }

        @Test
        void should_return_non_null_date_time_with_provided_null_timezone() {
            // given
            val timezone = (TimeZone) null;

            // when
            val result = timeService.now(timezone);

            // then
            assertThat(result)
                .isNotNull();
        }

        @Test
        void should_return_non_null_date_time_with_provided_non_null_timezone() {
            // given
            val timezone = TimeZone.getTimeZone("PST");

            // when
            val result = timeService.now(timezone);

            // then
            assertThat(result)
                .isNotNull();
        }
    }

    @Nested
    class convertToDateRange {
        @Test
        void should_return_non_null_date_range_for_non_null_criteria_and_timezone() {
            // given
            val today = LocalDate.now();
            val start = today.minusWeeks(1);
            val criteria = new DateRangeCriteria(start, today);
            val timezone = TimeZone.getTimeZone("PST");

            // when
            val result = timeService.convertToDateRange(criteria, timezone);

            // then
            assertThat(result)
                .isNotNull()
                .satisfies(
                    dateRange -> {
                        assertThat(dateRange.start())
                            .describedAs("dateRange.start")
                            .isNotNull();
                    },
                    dateRange -> {
                        assertThat(dateRange.end())
                            .describedAs("dateRange.end")
                            .isNotNull();
                    }
                );
        }

        @Test
        void should_return_non_null_date_range_for_null_criteria_and_timezone() {
            // given
            val criteria = (DateRangeCriteria) null;
            val timezone = (TimeZone) null;

            // when
            val result = timeService.convertToDateRange(criteria, timezone);

            // then
            assertThat(result)
                .isNotNull()
                .satisfies(
                    dateRange -> {
                        assertThat(dateRange.start())
                            .describedAs("dateRange.start")
                            .isNotNull();
                    },
                    dateRange -> {
                        assertThat(dateRange.end())
                            .describedAs("dateRange.end")
                            .isNotNull();
                    }
                );
        }
    }
}
