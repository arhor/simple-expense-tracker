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
        val rates = findExchangeRates(conversionQuery)

        val baseCurrencyCode = conversionQuery.baseCurrency.currencyCode
        val termCurrencyCode = conversionQuery.termCurrency.currencyCode

        val sourceRate = rates[baseCurrencyCode]
        val targetRate = rates[termCurrencyCode]

        return createExchangeRate(conversionQuery, sourceRate, targetRate)
    }

    private fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate> {
        val dates = getQueryDates(conversionQuery)

        if (dates == null) {
            return loadedRates.keys.maxOrNull()?.let(loadedRates::get)
                ?: throw MonetaryException("There is no more recent exchange rate to rate on $PROVIDER_NAME provider.")
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
            throw MonetaryException(
                "There is not exchange on day %s to rate to rate on %s.".format(
                    dates.map { it.format(DateTimeFormatter.ISO_LOCAL_DATE) },
                    PROVIDER_NAME,
                )
            )
        }
    }

    private fun ConversionQuery.exchangeRateBuilder(): ExchangeRateBuilder {
        val scale = getExchangeContext("exchangerate.digit.fraction")
        return ExchangeRateBuilder(scale).setBase(baseCurrency).setTerm(termCurrency)
    }

    private fun createExchangeRate(
        query: ConversionQuery,
        sourceRate: ExchangeRate?,
        targetRate: ExchangeRate?,
    ): ExchangeRate? {
        return when {
            BASE_CURRENCY.let { query.baseCurrency == it && query.termCurrency == it } -> {
                query.exchangeRateBuilder().setFactor(DefaultNumberValue.ONE).build()
            }

            BASE_CURRENCY == query.baseCurrency -> {
                targetRate
            }

            BASE_CURRENCY == query.termCurrency -> {
                sourceRate?.let { reverse(it) }
            }

            else -> {
                val baseRate = getExchangeRate(
                    query.toBuilder().setTermCurrency(BASE_CURRENCY).build()
                )
                val termRate = getExchangeRate(
                    query.toBuilder().setBaseCurrency(BASE_CURRENCY).setTermCurrency(query.termCurrency).build()
                )
                if ((baseRate != null) && (termRate != null)) {
                    val factor = baseRate.factor * termRate.factor
                    query.exchangeRateBuilder().setFactor(factor).setRateChain(baseRate, termRate).build()
                }
                throw CurrencyConversionException(query.baseCurrency, query.termCurrency, sourceRate?.context)
            }
        }
    }

    private fun reverse(rate: ExchangeRate): ExchangeRate =
        ExchangeRateBuilder(rate)
            .setRate(rate)
            .setBase(rate.baseCurrency)
            .setTerm(rate.termCurrency)
            .setFactor(DefaultNumberValue.ONE / rate.factor)
            .build()

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
        operator fun NumberValue.times(that: NumberValue): NumberValue = multiply(this, that)
    }
}
