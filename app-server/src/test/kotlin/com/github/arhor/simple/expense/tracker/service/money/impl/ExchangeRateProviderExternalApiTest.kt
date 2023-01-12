package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.impl.ExchangeRateProviderExternalApi.Companion.ConversionRatesData
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import javax.money.convert.ConversionQueryBuilder

internal class ExchangeRateProviderExternalApiTest {

    private val http = mockk<RestTemplate>()
    private val exchangeRateProvider = ExchangeRateProviderExternalApi(
        applicationProps = mockk(relaxed = true),
        restTemplateBuilder = mockk {
            every { rootUri(any()) } returns this
            every { build() } returns http
        }
    )

    @Test
    fun `should return expected conversion rate in case exchange rate on the specified date is present`() {
        // given
        val currencyUSD = "USD"
        val currencyEUR = "EUR"
        val conversionDate = LocalDate.of(2022, 12, 1)
        val conversionRate = 1.5

        every { http.getForObject(any(), any<Class<ConversionRatesData>>(), *anyVararg()) } answers {
            ConversionRatesData(
                base = currencyEUR,
                date = conversionDate,
                rates = mapOf(currencyUSD to conversionRate)
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
            .returns(conversionRate, from { it!!.factor.doubleValueExact() })
    }

    @Test
    fun `should return null in case there are no exchange rate on the specified date`() {
        // given
        val currencyUSD = "USD"
        val currencyEUR = "EUR"
        val conversionDate = LocalDate.of(2022, 12, 1)

        every { http.getForObject(any(), any<Class<ConversionRatesData>>(), *anyVararg()) } answers {
            ConversionRatesData(
                base = currencyEUR,
                date = conversionDate,
                rates = emptyMap()
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
            .isNull()
    }
}
