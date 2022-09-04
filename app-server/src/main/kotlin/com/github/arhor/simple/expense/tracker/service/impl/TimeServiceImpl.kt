package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.DateRangeCriteria
import com.github.arhor.simple.expense.tracker.service.TimeService
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import com.github.arhor.simple.expense.tracker.util.createDateRange
import com.github.arhor.simple.expense.tracker.util.currentZonedDateTime
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.TimeZone

@Service
class TimeServiceImpl : TimeService {

    override fun now(): ZonedDateTime {
        return now(null)
    }

    override fun now(timezone: TimeZone?): ZonedDateTime {
        return currentZonedDateTime(timezone)
    }

    override fun convertToDateRange(criteria: DateRangeCriteria?, timezone: TimeZone?): TemporalRange<LocalDate> {
        return criteria?.let {
            createDateRange(
                it.startDate,
                it.endDate,
                timezone
            )
        } ?: createDateRange(null, null, timezone)
    }
}
