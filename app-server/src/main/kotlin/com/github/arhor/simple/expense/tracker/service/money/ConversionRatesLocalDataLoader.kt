package com.github.arhor.simple.expense.tracker.service.money

import java.time.LocalDate

interface ConversionRatesLocalDataLoader {

    /**
     * Tries to find conversion-rates local data-file by the given year applying input stream handler in case file
     * present.
     *
     * @param year     year for which data should be loaded
     * @param consumer input stream handler which will be used in case data-file found
     **/
    fun loadConversionRatesDataByYear(year: Int, consumer: (Map<LocalDate, Map<String, Double>>) -> Unit)
}
