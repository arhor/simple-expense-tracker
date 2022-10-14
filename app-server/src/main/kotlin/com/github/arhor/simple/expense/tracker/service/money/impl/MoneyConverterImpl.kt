package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount
import javax.money.convert.ConversionQueryBuilder
import javax.money.convert.ExchangeRateProvider

@Service
class MoneyConverterImpl(private val exchangeRateProvider: ExchangeRateProvider) : MoneyConverter {

    @Override
    override fun <T : MonetaryAmount> convert(
        amount: T,
        currency: CurrencyUnit,
        conversionDate: LocalDate
    ): T {
        val query = ConversionQueryBuilder.of()
            .setBaseCurrency(amount.currency)
            .setTermCurrency(currency)
            .setProviderName(DEFAULT_PROVIDER_NAME)
            .set(conversionDate)
            .build()

        val currencyConversion = exchangeRateProvider.getCurrencyConversion(query)

        @Suppress("UNCHECKED_CAST")
        return amount.with(currencyConversion) as T
    }

    companion object {
        /**
         * Since [ExchangeRateProvider.isAvailable] default implementation checks only provider
         * name in the query, returning true in case no provider names specified, it should be
         * set manually.
         *
         * Reason: Consider [org.javamoney.moneta.spi.CompoundRateProvider] class - its
         * [ExchangeRateProvider.getExchangeRate] method iterates through the list of nested
         * providers, trying to determine which one to use via [ExchangeRateProvider.isAvailable]
         * method. Without provider name specified, it will use first provider in the list, even
         * though it may not provide exchange rates for the date set in the query.
         */
        const val DEFAULT_PROVIDER_NAME = "EXCHANGERATE_HOST"
    }
}
