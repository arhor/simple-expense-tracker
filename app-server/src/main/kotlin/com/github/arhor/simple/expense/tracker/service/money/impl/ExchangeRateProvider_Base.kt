package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.termCurrency
import org.javamoney.moneta.convert.ExchangeRateBuilder
import org.javamoney.moneta.spi.AbstractRateProvider
import org.javamoney.moneta.spi.DefaultNumberValue
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.NumberValue
import javax.money.convert.*

@Suppress("ClassName")
abstract class ExchangeRateProvider_Base(provider: String, rateType: RateType) : AbstractRateProvider(
    ProviderContextBuilder.of(
        provider,
        rateType,
    ).build()
) {
    protected val loadedRates = ConcurrentHashMap<LocalDate, Map<String, ExchangeRate>>()

    protected abstract fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate>?

    override fun getExchangeRate(conversionQuery: ConversionQuery): ExchangeRate? =
        findExchangeRates(conversionQuery)?.let {
            val sourceRate = it[conversionQuery.baseCurrency.currencyCode]
            val targetRate = it[conversionQuery.termCurrency.currencyCode]

            createExchangeRate(conversionQuery, sourceRate, targetRate)
        }

    protected fun save(baseCurrency: CurrencyUnit, data: Map<LocalDate, Map<String, Double>>) {
        for ((date, rates) in data.entries) {

            val mappings = HashMap<String, ExchangeRate>(rates.size)

            for ((term, rate) in rates.entries) {
                if (!Monetary.isCurrencyAvailable(term)) {
                    continue
                }

                val termCurrency = Monetary.getCurrency(term)
                val conversionFactor = DefaultNumberValue.of(rate)

                val exchangeRate =
                    ExchangeRateBuilder("PROVIDER_NAME", RateType.DEFERRED)
                        .setBase(baseCurrency)
                        .setTerm(termCurrency)
                        .setFactor(conversionFactor)
                        .build()

                mappings[term] = exchangeRate
            }
            loadedRates[date] = mappings
        }
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
                sourceRate?.reversed()
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

    private fun ConversionQuery.exchangeRateBuilder(): ExchangeRateBuilder =
        getExchangeContext("exchangerate.digit.fraction")
            .let(::ExchangeRateBuilder)
            .setBase(baseCurrency)
            .setTerm(termCurrency)

    private fun ExchangeRate.reversed(): ExchangeRate =
        ExchangeRateBuilder(this)
            .setRate(this)
            .setBase(termCurrency)
            .setTerm(baseCurrency)
            .setFactor(DefaultNumberValue.ONE / factor)
            .build()

    companion object {
        @JvmStatic
        protected val BASE_CURRENCY: CurrencyUnit = Monetary.getCurrency("EUR")

        protected operator fun NumberValue.div(that: NumberValue): NumberValue = divide(this, that)
        protected operator fun NumberValue.times(that: NumberValue): NumberValue = multiply(this, that)
    }
}
