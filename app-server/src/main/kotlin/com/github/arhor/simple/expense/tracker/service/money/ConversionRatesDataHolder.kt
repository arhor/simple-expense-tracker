package com.github.arhor.simple.expense.tracker.service.money

import java.time.LocalDate

@JvmInline
value class ConversionRatesDataHolder(val data: Map<LocalDate, Map<String, Double>>) {

    constructor(data: Pair<LocalDate, Map<String, Double>>) : this(mapOf(data))
}
