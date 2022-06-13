package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class ExchangerateHostRateProvider extends AbstractRateProvider {

    private static final int MIN_YEAR_PRELOADED = 2001;
    private static final int MAX_YEAR_PRELOADED = 2021;

    private static final String DATA_ID = ExchangerateHostRateProvider.class.getSimpleName();
    private static final CurrencyUnit BASE_CURRENCY = Monetary.getCurrency("EUR");

    private static final String PROVIDER_NAME = "EXCHANGERATE_HOST";
    private static final RateType PROVIDER_RATE_TYPE = RateType.DEFERRED;
    private static final ProviderContext CONTEXT = ProviderContextBuilder.of(PROVIDER_NAME, PROVIDER_RATE_TYPE)
        .set("providerDescription", "exchangerate.host API")
        .set("days", 1)
        .build();

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final Map<LocalDate, Map<String, ExchangeRate>> loadedRates = new ConcurrentHashMap<>();

    @Autowired
    public ExchangerateHostRateProvider(final ResourceLoader resourceLoader, final ObjectMapper objectMapper) {
        super(CONTEXT);
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        final var yearsPreloaded = MAX_YEAR_PRELOADED - MIN_YEAR_PRELOADED;
        final var tasks = new ArrayList<CompletableFuture<Void>>(yearsPreloaded);

        for (int i = MIN_YEAR_PRELOADED; i <= MAX_YEAR_PRELOADED; i++) {
            final var year = i;
            tasks.add(
                CompletableFuture.runAsync(() -> {
                    final var path = "classpath:conversion-rates/defaults/exchange.host/" + year + ".json";
                    final var resource = resourceLoader.getResource(path);

                    newDataLoaded(path, resource::getInputStream);
                })
            );
        }
        CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new)).get();
    }

    @Override
    public ExchangeRate getExchangeRate(final ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery);

        if (loadedRates.isEmpty()) {
            return null;
        }

        final var result = findExchangeRate(conversionQuery);

        final var baseCurrencyCode = conversionQuery.getBaseCurrency().getCurrencyCode();
        final var termCurrencyCode = conversionQuery.getCurrency().getCurrencyCode();

        final var sourceRate = result.targets.get(baseCurrencyCode);
        final var targetRate = result.targets.get(termCurrencyCode);

        final var builder = getBuilder(conversionQuery);

        return createExchangeRate(conversionQuery, builder, sourceRate, targetRate);
    }

    private void newDataLoaded(final String resourceId, final InputStreamSource source) {
        try {
            final var response = objectMapper.readValue(source.get(), ExchangeRateData.class);

            for (final var localDateRates : response.rates.entrySet()) {
                final var date = localDateRates.getKey();
                final var rates = localDateRates.getValue();

                final var mappings = new HashMap<String, ExchangeRate>(rates.size());
                for (final var entry : rates.entrySet()) {
                    final var curr = entry.getKey();
                    final var rate = entry.getValue();

                    if (!Monetary.isCurrencyAvailable(curr)) {
                        continue;
                    }

                    final var baseCurrency = Monetary.getCurrency(response.base);
                    final var termCurrency = Monetary.getCurrency(curr);
                    final var conversionFactor = DefaultNumberValue.of(rate);

                    final var exchangeRate =
                        new ExchangeRateBuilder(PROVIDER_NAME, PROVIDER_RATE_TYPE)
                            .setBase(baseCurrency)
                            .setTerm(termCurrency)
                            .setFactor(conversionFactor)
                            .build();

                    mappings.put(curr, exchangeRate);
                }
                loadedRates.put(date, mappings);
            }
            log.info("[SUCCESS]: {}", resourceId);
        } catch (final Exception e) {
            log.error("[FAILURE]: {}", resourceId, e);
        }
    }

    private RateResult findExchangeRate(final ConversionQuery conversionQuery) {
        final var dates = getQueryDates(conversionQuery);

        // TODO: try to load missing date rates from https://api.exchangerate.host/{date} (format YYYY-MM-DD)

        if (dates == null) {
            final var comparator = Comparator.<LocalDate>naturalOrder();
            final var targetDateRates = loadedRates.keySet()
                .stream()
                .max(comparator)
                .map(loadedRates::get)
                .orElseThrow(
                    () -> new MonetaryException(
                        "There is no more recent exchange rate to rate on %s provider.".formatted(DATA_ID)
                    )
                );
            return new RateResult(targetDateRates);
        } else {
            for (final var date : dates) {
                final var targets = loadedRates.get(date);

                if (targets != null) {
                    return new RateResult(targets);
                }
            }
            final var errorDates = Stream.of(dates)
                .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toList();
            throw new MonetaryException(
                "There is not exchange on day %s to rate to rate on %s.".formatted(
                    errorDates,
                    DATA_ID
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

        final var baseCurrency = query.getBaseCurrency();
        final var termCurrency = query.getCurrency();

        if (BASE_CURRENCY.equals(termCurrency)) {
            return (sourceRate != null) ? reverse(sourceRate) : null;
        } else if (BASE_CURRENCY.equals(baseCurrency)) {
            return targetRate;
        } else {
            final var rate1 = getExchangeRate(
                query.toBuilder()
                    .setTermCurrency(BASE_CURRENCY)
                    .build()
            );
            final var rate2 = getExchangeRate(
                query.toBuilder()
                    .setBaseCurrency(BASE_CURRENCY)
                    .setTermCurrency(termCurrency)
                    .build()
            );
            if ((rate1 != null) && (rate2 != null)) {
                final var factor1 = rate1.getFactor();
                final var factor2 = rate2.getFactor();

                final var resultFactor = multiply(factor1, factor2);

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
        final var baseCurrency = query.getBaseCurrency();
        final var termCurrency = query.getCurrency();

        return BASE_CURRENCY.equals(baseCurrency)
            && BASE_CURRENCY.equals(termCurrency);
    }


    private ExchangeRateBuilder getBuilder(final ConversionQuery query) {
        final var scale = getExchangeContext("exchangerate.digit.fraction");

        final var baseCurrency = query.getBaseCurrency();
        final var termCurrency = query.getCurrency();

        return new ExchangeRateBuilder(scale)
            .setBase(baseCurrency)
            .setTerm(termCurrency);
    }

    private ExchangeRate reverse(final ExchangeRate rate) {
        Objects.requireNonNull(rate, "Rate null is not reversible.");

        final var sourceBaseCurrency = rate.getCurrency();
        final var sourceTermCurrency = rate.getBaseCurrency();
        final var sourceFactor = rate.getFactor();

        final var reversedFactor = divide(DefaultNumberValue.ONE, sourceFactor);

        return new ExchangeRateBuilder(rate)
            .setRate(rate)
            .setBase(sourceBaseCurrency)
            .setTerm(sourceTermCurrency)
            .setFactor(reversedFactor)
            .build();
    }

    private record RateResult(Map<String, ExchangeRate> targets) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExchangeRateData(boolean success, String base, Map<LocalDate, Map<String, Double>> rates) {
    }

    @FunctionalInterface
    private interface InputStreamSource {
        InputStream get() throws IOException;
    }
}
