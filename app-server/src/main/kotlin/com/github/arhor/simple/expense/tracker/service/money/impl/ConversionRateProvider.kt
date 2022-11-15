package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesData
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesDataHolder
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import com.github.arhor.simple.expense.tracker.service.money.termCurrency
import org.javamoney.moneta.convert.ExchangeRateBuilder
import org.javamoney.moneta.spi.AbstractRateProvider
import org.javamoney.moneta.spi.DefaultNumberValue
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.lang.invoke.MethodHandles
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryException
import javax.money.NumberValue
import javax.money.convert.ConversionQuery
import javax.money.convert.CurrencyConversionException
import javax.money.convert.ExchangeRate
import javax.money.convert.ProviderContextBuilder
import javax.money.convert.RateType

@Service
class ConversionRateProvider(
    private val conversionRatesLocalDataLoader: ConversionRatesLocalDataLoader,
    restTemplateBuilder: RestTemplateBuilder,
) : AbstractRateProvider(
    ProviderContextBuilder.of(PROVIDER_NAME, PROVIDER_RATE_TYPE)
        .set("providerDescription", "exchangerate.host API")
        .set("days", 1)
        .build()
) {

    private val http = restTemplateBuilder.rootUri(EXCHANGERATE_HOST_URL).build()

    private val loadedRates = ConcurrentHashMap<LocalDate, Map<String, ExchangeRate>>()
    private val yearsAvailableLocally = Collections.synchronizedSet(HashSet<Int>())

    @PostConstruct
    fun init() {
        conversionRatesLocalDataLoader.loadInitialConversionRates {
            save(BASE_CURRENCY, it)
        }
    }

    override fun getExchangeRate(conversionQuery: ConversionQuery): ExchangeRate? {
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

    private fun findExchangeRate(conversionQuery: ConversionQuery): Map<String, ExchangeRate> {
        val dates = getQueryDates(conversionQuery)

        if (dates == null) {
            return loadedRates.keys
                .stream()
                .max(Comparator.naturalOrder())
                .map(loadedRates::get)
                .orElseThrow {
                    MonetaryException(
                        "There is no more recent exchange rate to rate on $PROVIDER_NAME provider."
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

                val data = http.getForObject(
                    "/{date}",
                    ConversionRatesData::class.java,
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )

                if (data != null) {
                    logger.info("Additionally loaded rates for the: {}", date)
                    save(Monetary.getCurrency(data.base), ConversionRatesDataHolder(date to data.rates))
                    return loadedRates[date] ?: emptyMap()
                }
            }
            val errorDates = dates.map { it.format(DateTimeFormatter.ISO_LOCAL_DATE) }

            throw MonetaryException(
                "There is not exchange on day $errorDates to rate to rate on $PROVIDER_NAME."
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
            && BASE_CURRENCY == query.termCurrency
    }

    private fun getBuilder(query: ConversionQuery): ExchangeRateBuilder {
        val scale = getExchangeContext("exchangerate.digit.fraction")

        val baseCurrency = query.baseCurrency
        val termCurrency = query.termCurrency

        return ExchangeRateBuilder(scale)
            .setBase(baseCurrency)
            .setTerm(termCurrency)
    }

    private fun reverse(rate: ExchangeRate): ExchangeRate {
        val sourceBaseCurrency = rate.baseCurrency
        val sourceTermCurrency = rate.termCurrency
        val sourceFactor = rate.factor

        val reversedFactor = DefaultNumberValue.ONE / sourceFactor

        return ExchangeRateBuilder(rate)
            .setRate(rate)
            .setBase(sourceBaseCurrency)
            .setTerm(sourceTermCurrency)
            .setFactor(reversedFactor)
            .build()
    }

    private fun save(baseCurrency: CurrencyUnit, input: ConversionRatesDataHolder) {
        for ((date, rates) in input.data.entries) {

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

    companion object {
        private val BASE_CURRENCY = Monetary.getCurrency("EUR")
        private val PROVIDER_RATE_TYPE = RateType.DEFERRED

        private const val EXCHANGERATE_HOST_URL = "https://api.exchangerate.host"
        private const val PROVIDER_NAME = "EXCHANGERATE_HOST"

        private val logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        operator fun NumberValue.div(that: NumberValue): NumberValue = divide(this, that)
    }
}
