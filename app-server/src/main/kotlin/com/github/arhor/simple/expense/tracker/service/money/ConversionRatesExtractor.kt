package com.github.arhor.simple.expense.tracker.service.money

import org.springframework.core.io.Resource

interface ConversionRatesExtractor {

    /**
     * Extracts conversion rates data from the file represented by the given resource.
     *
     * @param  resource resource containing conversion rates
     * @return conversion rates grouped by date
     */
    fun extractConversionRates(resource: Resource): ConversionRatesDataHolder
}
