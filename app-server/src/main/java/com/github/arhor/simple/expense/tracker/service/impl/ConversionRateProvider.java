package com.github.arhor.simple.expense.tracker.service.impl;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.arhor.simple.expense.tracker.config.properties.ConversionRatesConfigurationProperties;

import static java.util.Arrays.sort;
import static java.util.Comparator.comparing;

@Slf4j
@Service
public class ConversionRateProvider extends AbstractRateProvider {

    private static final CurrencyUnit BASE_CURRENCY = Monetary.getCurrency("EUR");
    private static final String COL_DATE = "date";
    private static final String PROVIDER_NAME = "EXCHANGERATE_HOST";
    private static final RateType PROVIDER_RATE_TYPE = RateType.DEFERRED;
    private static final ProviderContext CONTEXT = ProviderContextBuilder.of(PROVIDER_NAME, PROVIDER_RATE_TYPE)
        .set("providerDescription", "exchangerate.host API")
        .set("days", 1)
        .build();

    private final ConversionRatesConfigurationProperties conversionRatesConfigurationProperties;
    private final ResourcePatternResolver resourcePatternResolver;
    private final RestTemplate restTemplate;

    private final Map<LocalDate, Map<String, ExchangeRate>> loadedRates = new ConcurrentHashMap<>();
    private final Set<Integer> yearsAvailableLocally = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public ConversionRateProvider(
        final ResourcePatternResolver resourcePatternResolver,
        final RestTemplate restTemplate,
        final ConversionRatesConfigurationProperties conversionRatesConfigurationProperties
    ) {
        super(CONTEXT);
        this.resourcePatternResolver = resourcePatternResolver;
        this.restTemplate = restTemplate;
        this.conversionRatesConfigurationProperties = conversionRatesConfigurationProperties;
    }

    @PostConstruct
    public void init() throws IOException {
        val dataFilePattern = conversionRatesConfigurationProperties.dataFilePattern();
        val yearsToLoad = conversionRatesConfigurationProperties.yearsToLoad();
        val resources = resourcePatternResolver.getResources(dataFilePattern);

        val tasks = new CompletableFuture[yearsToLoad];

        sort(resources, comparing((final Resource resource) -> {
            val filename = resource.getFilename();

            if (filename != null) {
                try {
                    val yearString = filename.replace(".csv", "");
                    val year = Integer.parseInt(yearString);

                    yearsAvailableLocally.add(year);
                } catch (NumberFormatException e) {
                    log.error("Conversion-rates filename must represent the year for which it contains data", e);
                }
                return filename;
            }
            return "";
        }).reversed());

        for (int i = 0, resourcesLength = resources.length; (i < resourcesLength) && (i < yearsToLoad); i++) {
            val resource = resources[i];

            tasks[i] = CompletableFuture.runAsync(() -> {
                handleLoadedData(
                    resource.getFilename(),
                    resource::getInputStream
                );
            });
        }
        CompletableFuture.allOf(tasks).join();
    }

    @Override
    public ExchangeRate getExchangeRate(final ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery);

        if (loadedRates.isEmpty()) {
            return null;
        }

        val result = findExchangeRate(conversionQuery);

        val baseCurrencyCode = conversionQuery.getBaseCurrency().getCurrencyCode();
        val termCurrencyCode = conversionQuery.getCurrency().getCurrencyCode();

        val sourceRate = result.targets.get(baseCurrencyCode);
        val targetRate = result.targets.get(termCurrencyCode);

        val builder = getBuilder(conversionQuery);

        return createExchangeRate(conversionQuery, builder, sourceRate, targetRate);
    }

    private RateResult findExchangeRate(final ConversionQuery conversionQuery) {
        val dates = getQueryDates(conversionQuery);

        if (dates == null) {
            val targetDateRates = loadedRates.keySet()
                .stream()
                .max(Comparator.naturalOrder())
                .map(loadedRates::get)
                .orElseThrow(
                    () -> new MonetaryException(
                        "There is no more recent exchange rate to rate on %s provider.".formatted(
                            PROVIDER_NAME
                        )
                    )
                );
            return new RateResult(targetDateRates);
        } else {
            for (val date : dates) {
                var targets = loadedRates.get(date);
                val year = date.getYear();

                if (targets != null) {
                    return new RateResult(targets);
                } else if (yearsAvailableLocally.contains(year)) {
                    try {
                        val dataFilePattern = conversionRatesConfigurationProperties.dataFilePattern();
                        val resources = resourcePatternResolver.getResources(dataFilePattern);

                        for (val resource : resources) {
                            val filename = resource.getFilename();
                            val stringYear = String.valueOf(year);

                            if ((filename != null) && filename.contains(stringYear)) {
                                handleLoadedData(
                                    filename,
                                    resource::getInputStream
                                );
                                break;
                            }
                        }
                        targets = loadedRates.get(date);

                        if (targets != null) {
                            return new RateResult(targets);
                        } else {
                            log.warn("Local data-files does not contain data fro the date: {}", date);
                        }
                    } catch (IOException e) {
                        log.warn("Failed to load conversion-rates from internal data-files", e);
                    }
                }

                val response = restTemplate.getForEntity(
                    "https://api.exchangerate.host/{date}",
                    ExchangeRateData.class,
                    Map.of("date", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    val data = response.getBody();
                    if (data != null) {
                        log.info("Additionally loaded rates for the: {}", date);

                        val rates = save(date, Monetary.getCurrency(data.base), data.rates);

                        return new RateResult(rates);
                    }
                } else {
                    log.warn("Failed to load conversion-rates from external API");
                }
            }
            val errorDates = Stream.of(dates)
                .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toList();
            throw new MonetaryException(
                "There is not exchange on day %s to rate to rate on %s.".formatted(
                    errorDates,
                    PROVIDER_NAME
                )
            );
        }
    }

    private ExchangeRate createExchangeRate(
        final ConversionQuery query,
        final ExchangeRateBuilder builder,
        final ExchangeRate sourceRate,
        final ExchangeRate targetRate
    ) {
        if (areBothBaseCurrencies(query)) {
            return builder.setFactor(DefaultNumberValue.ONE).build();
        }

        val baseCurrency = query.getBaseCurrency();
        val termCurrency = query.getCurrency();

        if (BASE_CURRENCY.equals(termCurrency)) {
            return (sourceRate != null) ? reverse(sourceRate) : null;
        } else if (BASE_CURRENCY.equals(baseCurrency)) {
            return targetRate;
        } else {
            val rate1 = getExchangeRate(
                query.toBuilder()
                    .setTermCurrency(BASE_CURRENCY)
                    .build()
            );
            val rate2 = getExchangeRate(
                query.toBuilder()
                    .setBaseCurrency(BASE_CURRENCY)
                    .setTermCurrency(termCurrency)
                    .build()
            );
            if ((rate1 != null) && (rate2 != null)) {
                val factor1 = rate1.getFactor();
                val factor2 = rate2.getFactor();

                val resultFactor = multiply(factor1, factor2);

                return builder.setFactor(resultFactor).setRateChain(rate1, rate2).build();
            }
            throw new CurrencyConversionException(
                baseCurrency,
                termCurrency,
                sourceRate.getContext()
            );
        }
    }

    private boolean areBothBaseCurrencies(final ConversionQuery query) {
        val baseCurrency = query.getBaseCurrency();
        val termCurrency = query.getCurrency();

        return BASE_CURRENCY.equals(baseCurrency)
            && BASE_CURRENCY.equals(termCurrency);
    }

    private ExchangeRateBuilder getBuilder(final ConversionQuery query) {
        val scale = getExchangeContext("exchangerate.digit.fraction");

        val baseCurrency = query.getBaseCurrency();
        val termCurrency = query.getCurrency();

        return new ExchangeRateBuilder(scale)
            .setBase(baseCurrency)
            .setTerm(termCurrency);
    }

    private ExchangeRate reverse(final ExchangeRate rate) {
        Objects.requireNonNull(rate, "Rate null is not reversible.");

        val sourceBaseCurrency = rate.getCurrency();
        val sourceTermCurrency = rate.getBaseCurrency();
        val sourceFactor = rate.getFactor();

        val reversedFactor = divide(DefaultNumberValue.ONE, sourceFactor);

        return new ExchangeRateBuilder(rate)
            .setRate(rate)
            .setBase(sourceBaseCurrency)
            .setTerm(sourceTermCurrency)
            .setFactor(reversedFactor)
            .build();
    }

    private void handleLoadedData(final String resourceId, final InputStreamSource source) {
        try (val csvReader = NamedCsvReader.builder().build(new InputStreamReader(source.get()))) {
            for (val csvRow : csvReader) {
                val currentRowFields = csvRow.getFields();
                val rates = new HashMap<String, Double>(currentRowFields.size() - 1);

                var date = (LocalDate) null;

                for (val entry : currentRowFields.entrySet()) {
                    val name = entry.getKey();
                    val value = entry.getValue();

                    if (COL_DATE.equals(name)) {
                        if (date == null) {
                            date = LocalDate.parse(value);
                        } else {
                            throw new IllegalStateException(
                                "There must be only one column '%s' in the row: %s".formatted(
                                    COL_DATE,
                                    csvRow
                                )
                            );
                        }
                    } else {
                        if (isPresent(value)) {
                            rates.put(name, Double.valueOf(value));
                        }
                    }
                }

                if (date == null) {
                    throw new IllegalStateException(
                        "There must be exactly one column '%s' in the row: %s".formatted(
                            COL_DATE,
                            csvRow
                        )
                    );
                }

                save(date, BASE_CURRENCY, rates);
            }
            log.info("[SUCCESS]: {}", resourceId);
        } catch (Exception e) {
            log.error("[FAILURE]: {}", resourceId, e);
        }
    }

    private Map<String, ExchangeRate> save(
        final LocalDate date,
        final CurrencyUnit baseCurrency,
        final Map<String, Double> rates
    ) {
        var mappings = new HashMap<String, ExchangeRate>(rates.size());

        for (val entry : rates.entrySet()) {
            final var term = entry.getKey();
            final var rate = entry.getValue();

            if (!Monetary.isCurrencyAvailable(term)) {
                continue;
            }

            val termCurrency = Monetary.getCurrency(term);
            val conversionFactor = DefaultNumberValue.of(rate);

            val exchangeRate =
                new ExchangeRateBuilder(PROVIDER_NAME, PROVIDER_RATE_TYPE)
                    .setBase(baseCurrency)
                    .setTerm(termCurrency)
                    .setFactor(conversionFactor)
                    .build();

            mappings.put(term, exchangeRate);
        }
        loadedRates.put(date, mappings);
        return mappings;
    }

    private boolean isPresent(final String value) {
        return value != null
            && !value.isEmpty()
            && !value.isBlank();
    }

    private record RateResult(Map<String, ExchangeRate> targets) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExchangeRateData(String base, LocalDate date, Map<String, Double> rates) {
    }

    @FunctionalInterface
    private interface InputStreamSource {
        InputStream get() throws IOException;
    }
}
