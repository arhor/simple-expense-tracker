package com.github.arhor.simple.expense.tracker.service.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.util.TimeZone

fun zoneIdOrDefaultUTC(timezone: TimeZone?): ZoneId {
    return if (timezone != null) timezone.toZoneId() else ZoneOffset.UTC
}

fun currentZonedDateTime(): ZonedDateTime {
    return currentZonedDateTime(null)
}

fun currentZonedDateTime(timezone: TimeZone?): ZonedDateTime {
    return ZonedDateTime.now(zoneIdOrDefaultUTC(timezone)).truncatedTo(ChronoUnit.MILLIS)
}

fun currentLocalDateTime(): LocalDateTime {
    return currentLocalDateTime(null)
}

fun currentLocalDateTime(timezone: TimeZone?): LocalDateTime {
    return LocalDateTime.now(zoneIdOrDefaultUTC(timezone)).truncatedTo(ChronoUnit.MILLIS)
}

fun createDateRange(
    startDate: LocalDate?,
    endDate: LocalDate?,
    timezone: TimeZone?,
): TemporalRange<LocalDate> {

    val zoneId = zoneIdOrDefaultUTC(timezone)

    val safeStartDate = startDate ?: (endDate ?: LocalDate.now(zoneId)).with(firstDayOfMonth())
    val safeEndDate = endDate ?: (startDate ?: LocalDate.now(zoneId)).with(lastDayOfMonth())

    if (safeStartDate.isAfter(safeEndDate)) {
        throw IllegalStateException(
            "Required start date less than or equal end date, found: start = $safeStartDate, end = $safeEndDate"
        )
    }
    return TemporalRange(safeStartDate, safeEndDate)
}
