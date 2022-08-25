package com.github.arhor.simple.expense.tracker.service.money.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import org.javamoney.moneta.convert.ExchangeRateBuilder
import org.javamoney.moneta.spi.AbstractRateProvider
import org.javamoney.moneta.spi.DefaultNumberValue
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.lang.invoke.MethodHandles
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import javax.annotation.PostConstruct
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryException
import javax.money.convert.ConversionQuery
import javax.money.convert.CurrencyConversionException
import javax.money.convert.ExchangeRate
import javax.money.convert.ProviderContextBuilder
import javax.money.convert.RateType

@Service
class ConversionRateProvider(
    private val conversionRatesLocalDataLoader: ConversionRatesLocalDataLoader,
    private val restTemplateBuilder: RestTemplateBuilder,
) : AbstractRateProvider(
    ProviderContextBuilder.of(PROVIDER_NAME, PROVIDER_RATE_TYPE)
        .set("providerDescription", "exchangerate.host API")
        .set("days", 1)
        .build()
) {

    private val http = restTemplateBuilder.rootUri(EXCHANGERATE_HOST_URL).build()

    private val loadedRates =  ConcurrentHashMap<LocalDate, Map<String, ExchangeRate>>()
    private val yearsAvailableLocally = Collections.synchronizedSet(HashSet<Int>())

    @PostConstruct
    fun init() {
        conversionRatesLocalDataLoader.loadInitialConversionRates {
            save(BASE_CURRENCY, it)
        }
    }

    override fun getExchangeRate(  conversionQuery: ConversionQuery): ExchangeRate? {
        if (loadedRates.isEmpty()) {
            return null
        }
        val result = findExchangeRate(conversionQuery)

        val baseCurrencyCode = conversionQuery.baseCurrency.currencyCode
        val termCurrencyCode = conversionQuery.currency.currencyCode

        val sourceRate = result[baseCurrencyCode]
        val targetRate = result[termCurrencyCode]

        val builder = getBuilder(conversionQuery)

        return createExchangeRate(conversionQuery, builder, sourceRate, targetRate)
    }

    private fun findExchangeRate(  conversionQuery: ConversionQuery): Map<String, ExchangeRate> {
        val dates = getQueryDates(conversionQuery)

        if (dates == null) {
            return loadedRates.keys
                .stream()
                .max(Comparator.naturalOrder())
                .map(loadedRates::get)
                .orElseThrow {
                    MonetaryException(
                        "There is no more recent exchange rate to rate on %s provider.".formatted(
                            PROVIDER_NAME
                        )
                    )
                }!!
        } else {
            for (date in dates) {
                var targets = loadedRates[date]
                val year = date.year

                if (targets != null) {
                    return targets
                } else if (yearsAvailableLocally.contains(year)) {
                    conversionRatesLocalDataLoader.loadConversionRatesDataByYear(year) {
                        save(BASE_CURRENCY, it)
                    }

                    targets = loadedRates[date]

                    if (targets != null) {
                        return targets
                    } else {
                        logger.warn("Local data-files does not contain data for the date: {}", date)
                    }
                }

                val response = http.getForEntity(
                    "/{date}",
                    ExchangeRateData.class,
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )

                if (response.getStatusCode() == HttpStatus.OK) {
                    val data = response.getBody()
                    if (data != null) {
                        logger.info("Additionally loaded rates for the: {}", date)
                        save(Monetary.getCurrency(data.base), mapOf(date, data.rates))
                        return loadedRates.get(date)
                    }
                } else {
                    logger.warn("Failed to load conversion-rates from external API")
                }
            }
            val errorDates = Stream.of(dates)
                .map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .toList()
            throw new MonetaryException(
                "There is not exchange on day %s to rate to rate on %s.".formatted(
                    errorDates,
                    PROVIDER_NAME
                )
            )
        }
    }

    private fun createExchangeRate(
        query: ConversionQuery,
        builder: ExchangeRateBuilder,
        sourceRate: ExchangeRate?,
        targetRate: ExchangeRate?,
    ): ExchangeRate? {
        if (areBothBaseCurrencies(query)) {
            return builder.setFactor(DefaultNumberValue.ONE).build()
        }

        val baseCurrency = query.baseCurrency
        val termCurrency = query.currency

        return if (termCurrency == BASE_CURRENCY) {
            sourceRate?.let { reverse(it) }
        } else if (baseCurrency == BASE_CURRENCY) {
            targetRate
        } else {
            val rate1 = getExchangeRate(
                query.toBuilder()
                    .setTermCurrency(BASE_CURRENCY)
                    .build()
            )
            val rate2 = getExchangeRate(
                query.toBuilder()
                    .setBaseCurrency(BASE_CURRENCY)
                    .setTermCurrency(termCurrency)
                    .build()
            )
            if ((rate1 != null) && (rate2 != null)) {
                val factor1 = rate1.factor
                val factor2 = rate2.factor

                val resultFactor = multiply(factor1, factor2)

                builder
                    .setFactor(resultFactor)
                    .setRateChain(rate1, rate2)
                    .build()
            }
            throw CurrencyConversionException(
                baseCurrency,
                termCurrency,
                sourceRate?.context,
            )
        }
    }

    private fun areBothBaseCurrencies(query: ConversionQuery): Boolean {
        return BASE_CURRENCY == query.baseCurrency
            && BASE_CURRENCY == query.currency
    }

    private fun getBuilder(query: ConversionQuery): ExchangeRateBuilder {
        val scale = getExchangeContext("exchangerate.digit.fraction")

        val baseCurrency = query.baseCurrency
        val termCurrency = query.currency

        return ExchangeRateBuilder(scale)
            .setBase(baseCurrency)
            .setTerm(termCurrency)
    }

    private fun reverse(rate: ExchangeRate): ExchangeRate {
        val sourceBaseCurrency = rate.currency
        val sourceTermCurrency = rate.baseCurrency
        val sourceFactor = rate.factor

        val reversedFactor = divide(DefaultNumberValue.ONE, sourceFactor)

        return ExchangeRateBuilder(rate)
            .setRate(rate)
            .setBase(sourceBaseCurrency)
            .setTerm(sourceTermCurrency)
            .setFactor(reversedFactor)
            .build()
    }

    private fun save(baseCurrency: CurrencyUnit, input: Map<LocalDate, Map<String, Double>>) {
        for ((date, rates) in input.entries) {

            val mappings = HashMap<String, ExchangeRate>(rates.size)

            for ((term, rate) in rates.entries) {
                if (!Monetary.isCurrencyAvailable(term)) {
                    continue
                }

                val termCurrency = Monetary.getCurrency(term)
                val conversionFactor = DefaultNumberValue.of(rate)

                val exchangeRate =
                    ExchangeRateBuilder(PROVIDER_NAME, PROVIDER_RATE_TYPE)
                        .setBase(baseCurrency)
                        .setTerm(termCurrency)
                        .setFactor(conversionFactor)
                        .build()

                mappings[term] = exchangeRate
            }
            loadedRates[date] = mappings
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ExchangeRateData(val base: String, val date: LocalDate, val rates: Map<String, Double>)

    companion object {
        private val BASE_CURRENCY = Monetary.getCurrency("EUR")
        private val PROVIDER_RATE_TYPE = RateType.DEFERRED

        private const val EXCHANGERATE_HOST_URL = "https://api.exchangerate.host"
        private const val PROVIDER_NAME = "EXCHANGERATE_HOST"

        private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
