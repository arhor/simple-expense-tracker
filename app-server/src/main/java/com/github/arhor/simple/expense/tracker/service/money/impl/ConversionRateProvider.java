package com.github.arhor.simple.expense.tracker.service.money.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader;

@Slf4j
@Service
public class ConversionRateProvider extends AbstractRateProvider {

    private static final CurrencyUnit BASE_CURRENCY = Monetary.getCurrency("EUR");
    private static final String PROVIDER_NAME = "EXCHANGERATE_HOST";
    private static final RateType PROVIDER_RATE_TYPE = RateType.DEFERRED;

    private final ConversionRatesLocalDataLoader conversionRatesLocalDataLoader;
    private final RestTemplate restTemplate;

    private final Map<LocalDate, Map<String, ExchangeRate>> loadedRates = new ConcurrentHashMap<>();
    private final Set<Integer> yearsAvailableLocally = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    public ConversionRateProvider(
        final ConversionRatesLocalDataLoader conversionRatesLocalDataLoader,
        final RestTemplate restTemplate
    ) {
        super(
            ProviderContextBuilder.of(PROVIDER_NAME, PROVIDER_RATE_TYPE)
                .set("providerDescription", "exchangerate.host API")
                .set("days", 1)
                .build()
        );
        this.conversionRatesLocalDataLoader = conversionRatesLocalDataLoader;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        conversionRatesLocalDataLoader.loadInitialConversionRates(data -> {
            save(BASE_CURRENCY, data);
        });
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

        val sourceRate = result.get(baseCurrencyCode);
        val targetRate = result.get(termCurrencyCode);

        val builder = getBuilder(conversionQuery);

        return createExchangeRate(conversionQuery, builder, sourceRate, targetRate);
    }

    private Map<String, ExchangeRate> findExchangeRate(final ConversionQuery conversionQuery) {
        val dates = getQueryDates(conversionQuery);

        if (dates == null) {
            return loadedRates.keySet()
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
        } else {
            for (val date : dates) {
                var targets = loadedRates.get(date);
                val year = date.getYear();

                if (targets != null) {
                    return targets;
                } else if (yearsAvailableLocally.contains(year)) {
                    conversionRatesLocalDataLoader.loadConversionRatesDataByYear(year, data -> {
                        save(BASE_CURRENCY, data);
                    });

                    targets = loadedRates.get(date);

                    if (targets != null) {
                        return targets;
                    } else {
                        log.warn("Local data-files does not contain data for the date: {}", date);
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
                        save(Monetary.getCurrency(data.base), Map.of(date, data.rates));
                        return loadedRates.get(date);
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

    private void save(
        final CurrencyUnit baseCurrency,
        final Map<LocalDate, Map<String, Double>> input
    ) {
        for (Map.Entry<LocalDate, Map<String, Double>> e : input.entrySet()) {
            val date = e.getKey();
            val rates = e.getValue();

            var mappings = new HashMap<String, ExchangeRate>(rates.size());

            for (val entry : rates.entrySet()) {
                val term = entry.getKey();
                val rate = entry.getValue();

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
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ExchangeRateData(String base, LocalDate date, Map<String, Double> rates) {
    }
}
