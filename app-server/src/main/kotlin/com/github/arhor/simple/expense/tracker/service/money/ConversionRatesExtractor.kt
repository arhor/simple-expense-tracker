package com.github.arhor.simple.expense.tracker.service.money

import org.springframework.core.io.Resource
import java.time.LocalDate

interface ConversionRatesExtractor {

    /**
     * Extracts conversion rates data from the file represented by the given resource.
     *
     * @param  resource resource containing conversion rates
     * @return conversion rates grouped by date
     */
    fun extractConversionRates(resource: Resource): Map<LocalDate, Map<String, Double>>
}
