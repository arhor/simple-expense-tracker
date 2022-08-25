package com.github.arhor.simple.expense.tracker.service

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import javax.validation.constraints.PastOrPresent

/**
 * Represents date-range query-string parameters.
 *
 * @param startDate start of the date-range, nullable
 * @param endDate   end of the date-range, nullable
 */
data class DateRangeCriteria(
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate?,

    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate?
)
