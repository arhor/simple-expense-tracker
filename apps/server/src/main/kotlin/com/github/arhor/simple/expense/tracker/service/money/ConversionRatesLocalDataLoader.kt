package com.github.arhor.simple.expense.tracker.service.money

import java.time.LocalDate

interface ConversionRatesLocalDataLoader {

    /**
     * Tries to find conversion-rates local data-file by the given year applying input stream handler in case file
     * present.
     *
     * @param year for which data should be loaded
     **/
    fun loadConversionRatesDataByYear(year: Int): Map<LocalDate, Map<String, Double>>
}
