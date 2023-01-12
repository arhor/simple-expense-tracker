package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.LocalDate
import javax.money.convert.ConversionQueryBuilder

class ExchangeRateProviderLocalDataTest {

    private val localDataLoader = mockk<ConversionRatesLocalDataLoader>()
    private val exchangeRateProvider = ExchangeRateProviderLocalData(localDataLoader)

    @Test
    @TestFactory
    fun `should return expected conversion rate in case exchange rate on the specified date is present`() {
        // given
        val currencyUSD = "USD"
        val currencyEUR = "EUR"
        val conversionDate = LocalDate.of(2022, 12, 1)
        val conversionRate = 1.5

        every { localDataLoader.loadConversionRatesDataByYear(any()) } answers {
            mapOf(
                conversionDate to mapOf(
                    currencyUSD to conversionRate
                )
            )
        }

        // when
        val result = exchangeRateProvider.getExchangeRate(
            conversionQuery = ConversionQueryBuilder.of()
                .setBaseCurrency(currencyEUR)
                .setTermCurrency(currencyUSD)
                .set(conversionDate)
                .build()
        )

        // then
        assertThat(result)
            .isNotNull
            .returns(conversionRate, Assertions.from { it!!.factor.doubleValueExact() })
    }

    @Test
    fun `should return null in case there are no exchange rate on the specified date`() {
        // given
        val currencyUSD = "USD"
        val currencyEUR = "EUR"
        val conversionDate = LocalDate.of(2022, 12, 1)

        every { localDataLoader.loadConversionRatesDataByYear(any()) } returns emptyMap()

        // when
        val result = exchangeRateProvider.getExchangeRate(
            conversionQuery = ConversionQueryBuilder.of()
                .setBaseCurrency(currencyEUR)
                .setTermCurrency(currencyUSD)
                .set(conversionDate)
                .build()
        )

        // then
        assertThat(result)
            .isNull()
    }
}
