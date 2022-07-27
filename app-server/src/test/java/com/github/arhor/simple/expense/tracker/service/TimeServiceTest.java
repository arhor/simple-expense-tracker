package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(TimeServiceImpl.class)
class TimeServiceTest {

    @Autowired
    private TimeService timeService;

    @Test
    void should_return_non_null_date_time_with_default_timezone() {
        // when
        var result = timeService.now();

        // then
        assertThat(result)
            .isNotNull();
    }

    @Test
    void should_return_non_null_date_time_with_provided_null_timezone() {
        // given
        var timezone = (TimeZone) null;

        // when
        var result = timeService.now(timezone);

        // then
        assertThat(result)
            .isNotNull();
    }

    @Test
    void should_return_non_null_date_time_with_provided_non_null_timezone() {
        // given
        var timezone = TimeZone.getTimeZone("PST");

        // when
        var result = timeService.now(timezone);

        // then
        assertThat(result)
            .isNotNull();
    }

    @Test
    void should_return_non_null_date_range_for_non_null_criteria_and_timezone() {
        // given
        var today = LocalDate.now();
        var start = today.minusWeeks(1);
        var criteria = new DateRangeCriteria(start, today);
        var timezone = TimeZone.getTimeZone("PST");

        // when
        var result = timeService.convertToDateRange(criteria, timezone);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .isNotNull();
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .isNotNull();
                }
            );
    }

    @Test
    void should_return_non_null_date_range_for_null_criteria_and_timezone() {
        // given
        var criteria = (DateRangeCriteria) null;
        var timezone = (TimeZone) null;

        // when
        var result = timeService.convertToDateRange(criteria, timezone);

        // then
        assertThat(result)
            .isNotNull()
            .satisfies(
                dateRange -> {
                    assertThat(dateRange.start())
                        .isNotNull();
                },
                dateRange -> {
                    assertThat(dateRange.end())
                        .isNotNull();
                }
            );
    }
}
