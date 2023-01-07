package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter
import org.javamoney.moneta.spi.CompoundRateProvider
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount
import javax.money.convert.ConversionQueryBuilder
import javax.money.convert.ExchangeRateProvider

@Service
class MoneyConverterImpl(
    exchangeRateProviders: List<ExchangeRateProvider>,
    compoundProviderFactory: (List<ExchangeRateProvider>) -> CompoundRateProvider = ::CompoundRateProvider,
) : MoneyConverter {

    private val exchangeRateProvider = compoundProviderFactory(exchangeRateProviders)

    @Cacheable(cacheNames = ["currency-conversion-by-date"])
    override fun <T> convert(amount: T, currency: CurrencyUnit, conversionDate: LocalDate): MonetaryAmount
        where T : MonetaryAmount {

        val query = ConversionQueryBuilder.of()
            .setBaseCurrency(amount.currency)
            .setTermCurrency(currency)
            .set(conversionDate)
            .build()

        val currencyConversion = exchangeRateProvider.getCurrencyConversion(query)

        return amount.with(currencyConversion)
    }
}
