package com.github.arhor.simple.expense.tracker.web.api;

import java.time.LocalDate;

import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

public record DateRangeCriteria(
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,

    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {
}
