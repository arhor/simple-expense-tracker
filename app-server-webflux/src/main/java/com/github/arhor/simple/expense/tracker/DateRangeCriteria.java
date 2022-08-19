package com.github.arhor.simple.expense.tracker;

import java.time.LocalDate;

import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Represents date-range query-string parameters.
 *
 * @param startDate start of the date-range, nullable
 * @param endDate   end of the date-range, nullable
 */
public record DateRangeCriteria(
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,

    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
}
