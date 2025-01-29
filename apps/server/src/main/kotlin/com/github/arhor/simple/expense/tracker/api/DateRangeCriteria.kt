package com.github.arhor.simple.expense.tracker.api

import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.PastOrPresent
import org.springdoc.core.annotations.ParameterObject
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * Represents date-range query-string parameters.
 *
 * @param startDate start of the date-range, nullable
 * @param endDate   end of the date-range, nullable
 */
@ParameterObject
data class DateRangeCriteria(

    @field:Parameter(
        description = "Start of the date-range",
        example = "2023-01-01"
    )
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate?,

    @field:Parameter(
        description = "End of the date-range",
        example = "2023-01-31",
    )
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate?
)
