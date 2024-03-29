package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.termCurrency
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.javamoney.moneta.Money
import org.javamoney.moneta.spi.CompoundRateProvider
import org.junit.jupiter.api.Test
import java.time.LocalDate
import javax.money.Monetary
import javax.money.convert.ConversionQuery
import javax.money.convert.CurrencyConversion

internal class MoneyConverterImplTest {

    private val exchangeRateProvider = mockk<CompoundRateProvider>()
    private val moneyConverter = MoneyConverterImpl(
        exchangeRateProviders = emptyList(),
        compoundProviderFactory = { exchangeRateProvider }
    )

    @Test
    fun `should return passed value as-is when base and term currencies are equal`() {
        // given
        val currency = Monetary.getCurrency("EUR")
        val amount = Money.of(10, currency)
        val conversionDate = LocalDate.of(2022, 10, 13)
        val conversionOperator = mockk<CurrencyConversion>()
        val conversionQuery = slot<ConversionQuery>()

        every { exchangeRateProvider.getCurrencyConversion(any<ConversionQuery>()) } returns conversionOperator
        every { conversionOperator.apply(any()) } returnsArgument 0

        // when
        val result = moneyConverter.convert(amount, currency, conversionDate)

        // then
        verify(exactly = 1) { exchangeRateProvider.getCurrencyConversion(capture(conversionQuery)) }
        verify(exactly = 1) { conversionOperator.apply(amount) }

        assertThat(conversionQuery.captured)
            .returns(currency, from { it.baseCurrency })
            .returns(currency, from { it.termCurrency })
            .returns(conversionDate, from { it[LocalDate::class.java] })

        assertThat(result)
            .isEqualTo(amount)
    }
}
