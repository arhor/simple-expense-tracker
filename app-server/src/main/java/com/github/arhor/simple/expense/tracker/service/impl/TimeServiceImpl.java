package com.github.arhor.simple.expense.tracker.service.impl;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria;
import com.github.arhor.simple.expense.tracker.service.TimeService;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

import static com.github.arhor.simple.expense.tracker.util.TimeUtil.createDateRange;
import static com.github.arhor.simple.expense.tracker.util.TimeUtil.currentZonedDateTime;

@Service
public class TimeServiceImpl implements TimeService {

    @Override
    public ZonedDateTime now() {
        return now(null);
    }

    @Override
    public ZonedDateTime now(final TimeZone timezone) {
        return currentZonedDateTime(timezone);
    }

    @Override
    public TemporalRange<LocalDate> convertToDateRange(final DateRangeCriteria criteria, final TimeZone timezone) {
        return createDateRange(criteria.startDate(), criteria.endDate(), timezone);
    }
}
