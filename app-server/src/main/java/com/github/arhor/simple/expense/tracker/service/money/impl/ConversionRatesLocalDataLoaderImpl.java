package com.github.arhor.simple.expense.tracker.service.money.impl;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.config.properties.ConversionRatesProps;
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader;

import static java.util.Arrays.sort;
import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ConversionRatesLocalDataLoaderImpl implements ConversionRatesLocalDataLoader {

    private static final String COL_DATE = "date";

    private final ConversionRatesProps conversionRatesProps;
    private final ResourcePatternResolver resourcePatternResolver;

    @Override
    public void loadConversionRatesDataByYear(int year, Consumer<Map<LocalDate, Map<String, Double>>> consumer) {
        withLocalData(resources -> {
            for (val resource : resources) {
                val filename = resource.getFilename();

                if ((filename != null) && filename.contains(String.valueOf(year))) {
                    readDataFile(
                        year,
                        resource::getInputStream,
                        consumer
                    );
                    break;
                }
            }
            log.warn("Local data-files does not contain data for the year: {}", year);
        });
    }

    @Override
    public void loadInitialConversionRates(final Consumer<Map<LocalDate, Map<String, Double>>> consumer) {
        withLocalData(resources -> {
            val preload = conversionRatesProps.preload();
            val tasks = new CompletableFuture[preload];

            sort(resources, comparing(Resource::getFilename).reversed());

            for (int i = 0, resourcesLength = resources.length; (i < resourcesLength) && (i < preload); i++) {
                val resource = resources[i];
                val filename = resource.getFilename();

                if (filename != null) {
                    try {
                        tasks[i] = CompletableFuture.runAsync(() -> {
                            readDataFile(
                                Integer.parseInt(
                                    filename.replace(".csv", "")
                                ),
                                resource::getInputStream,
                                consumer
                            );
                        });
                    } catch (NumberFormatException e) {
                        log.error("Conversion-rates filename must represent the year for which it contains data", e);
                    }
                }
            }
            CompletableFuture.allOf(tasks).join();
        });
    }

    private void withLocalData(final Consumer<Resource[]> consumer) {
        try {
            consumer.accept(
                resourcePatternResolver.getResources(
                    conversionRatesProps.pattern()
                )
            );
        } catch (IOException e) {
            log.error("Failed to load conversion-rates from local data-files", e);
        }
    }

    private void readDataFile(
        final int year,
        final InputStreamSupplier data,
        final Consumer<Map<LocalDate, Map<String, Double>>> consumer
    ) {
        try (
            val csvReader = NamedCsvReader.builder().skipComments(true).build(
                new InputStreamReader(
                    data.get()
                )
            )
        ) {
            val length = determineMapCapacity(year);
            val result = new HashMap<LocalDate, Map<String, Double>>(length);

            for (val csvRow : csvReader) {
                handleCsvRow(csvRow, result::put);
            }
            consumer.accept(result);
            log.info("[SUCCESS]: {} year conversion rates loaded", year);
        } catch (IOException e) {
            log.warn("Failed to load rates for the year: {}", year, e);
        }
    }

    private void handleCsvRow(final NamedCsvRow csvRow, final BiConsumer<LocalDate, Map<String, Double>> consumer) {
        val currentRowFields = csvRow.getFields();
        val rates = new HashMap<String, Double>(currentRowFields.size() - 1);

        var date = (LocalDate) null;

        for (val field : currentRowFields.entrySet()) {
            val name = field.getKey();
            val value = field.getValue();

            if (COL_DATE.equals(name)) {
                if (date == null) {
                    date = LocalDate.parse(value);
                } else {
                    throwDateColumnException(csvRow);
                }
            } else if (isPresent(value)) {
                rates.put(name, Double.valueOf(value));
            }
        }
        if (date == null) {
            throwDateColumnException(csvRow);
        }
        consumer.accept(date, Map.copyOf(rates));
    }

    private boolean isPresent(final String value) {
        return value != null
            && !value.isEmpty()
            && !value.isBlank();
    }

    private int determineMapCapacity(final int year) {
        return IsoChronology.INSTANCE.isLeapYear(year)
            ? 366
            : 365;
    }

    private void throwDateColumnException(final NamedCsvRow csvRow) {
        throw new IllegalStateException(
            "There must be exactly one column '%s' in the row: %s".formatted(
                COL_DATE,
                csvRow
            )
        );
    }

    @FunctionalInterface
    private interface InputStreamSupplier {
        InputStream get() throws IOException;
    }
}
