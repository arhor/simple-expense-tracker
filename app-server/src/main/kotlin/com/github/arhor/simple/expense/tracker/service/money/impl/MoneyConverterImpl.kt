package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.MoneyConverter
import com.github.arhor.simple.expense.tracker.util.currentZonedDateTime
import org.springframework.stereotype.Service
import java.time.DayOfWeek
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
        date: LocalDate
    ): T {
        val sourceCurrency = amount.currency
        val conversionDate = determineConversionDate(date)
        val providerName = determineProviderName(conversionDate)

        val query = ConversionQueryBuilder.of()
            .setBaseCurrency(sourceCurrency)
            .setTermCurrency(currency)
            .setProviderName(providerName)
            .set(conversionDate)
            .build()

        val currencyConversion = exchangeRateProvider.getCurrencyConversion(query)

        @Suppress("UNCHECKED_CAST")
        return amount.with(currencyConversion) as T
    }

    /**
     * ECB does not provide exchange rates on Saturday, Sunday, and public holidays.
     *
     * @see <a href="https://www.ecb.europa.eu/services/contacts/working-hours/html/index.en.html">ECB working hours</a>
     */
    private fun determineConversionDate(date: LocalDate): LocalDate {
        // TODO: consider also public holidays according to the ECB
        return when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> date.minusDays(1)
            DayOfWeek.SUNDAY -> date.minusDays(2)
            else -> date
        }
    }

    /**
     * Since {@link ExchangeRateProvider#isAvailable} default implementation checks only provider name in the query,
     * returning {@literal true} in case no provider names specified, it should be set manually.
     *
     * <p>
     * <strong>Reason</strong>: Consider {@link org.javamoney.moneta.spi.CompoundRateProvider} class - its
     * {@link ExchangeRateProvider#getExchangeRate} method iterates through the list of nested providers, trying to
     * determine which one to use via {@link ExchangeRateProvider#isAvailable} method. Without provider name specified,
     * it will use first provider in the list, even though it may not provide exchange rates for the date set in the
     * query.
     * </p>
     */
    private fun determineProviderName(conversionDate: LocalDate): String {
        val currentDate = currentZonedDateTime().toLocalDate()
        val period = conversionDate.until(currentDate)
        val daysPassedFromConversion = period.days

        return when {
            daysPassedFromConversion <= 0 -> {
                "EXCHANGERATE_HOST"
            }

            daysPassedFromConversion <= 90 -> {
                "EXCHANGERATE_HOST"
            }

            else -> {
                "EXCHANGERATE_HOST"
            }
        }
    }
}
