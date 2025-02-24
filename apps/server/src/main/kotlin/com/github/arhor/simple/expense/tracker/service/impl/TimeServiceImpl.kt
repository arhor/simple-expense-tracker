package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.service.TimeService
import com.github.arhor.simple.expense.tracker.service.util.TemporalRange
import com.github.arhor.simple.expense.tracker.service.util.createDateRange
import com.github.arhor.simple.expense.tracker.service.util.currentZonedDateTime
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

    override fun convertToDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        timezone: TimeZone?
    ): TemporalRange<LocalDate> {
        return createDateRange(startDate, endDate, timezone)
    }
}
