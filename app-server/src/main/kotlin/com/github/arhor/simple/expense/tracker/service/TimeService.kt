package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.util.TemporalRange
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.TimeZone

interface TimeService {

    fun now(): ZonedDateTime

    fun now(timezone: TimeZone?): ZonedDateTime

    fun convertToDateRange(startDate: LocalDate?, endDate: LocalDate?, timezone: TimeZone?): TemporalRange<LocalDate>
}
