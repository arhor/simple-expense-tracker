package com.github.arhor.simple.expense.tracker.service.money;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

public interface ConversionRatesLocalDataLoader {

    /**
     * Tries to find conversion-rates local data-file by the given year applying input stream handler in case file
     * present.
     *
     * @param year    year for which data should be loaded
     * @param handler input stream handler which will be used in case data-file found
     **/
    void loadConversionRatesDataByYear(int year, Consumer<Map<LocalDate, Map<String, Double>>> handler);

    void loadInitialConversionRates(final Consumer<Map<LocalDate, Map<String, Double>>> consumer);
}
