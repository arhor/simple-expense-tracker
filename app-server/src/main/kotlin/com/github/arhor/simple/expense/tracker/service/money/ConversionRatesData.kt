package com.github.arhor.simple.expense.tracker.service.money

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConversionRatesData(
    /**
     * Base currency.
     */
    val base: String,

    /**
     * Conversion date.
     */
    val date: LocalDate,

    /**
     * Conversion rates.
     */
    val rates: Map<String, Double>,
)
