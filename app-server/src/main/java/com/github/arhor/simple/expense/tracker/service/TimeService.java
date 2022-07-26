package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import com.github.arhor.simple.expense.tracker.util.TemporalRange;

public interface TimeService {

    ZonedDateTime now();

    ZonedDateTime now(TimeZone timezone);

    TemporalRange<LocalDate> convertToDateRange(DateRangeCriteria criteria, TimeZone timezone);
}
