package com.github.arhor.simple.expense.tracker

import jakarta.validation.constraints.PastOrPresent
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

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
