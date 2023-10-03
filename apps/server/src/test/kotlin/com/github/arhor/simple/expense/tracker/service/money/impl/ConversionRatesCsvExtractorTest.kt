package com.github.arhor.simple.expense.tracker.service.money.impl

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.time.LocalDate

internal class ConversionRatesCsvExtractorTest {

    private val conversionRatesExtractor = ConversionRatesCsvExtractor()

    @Test
    fun `should extract expected conversion rates from the correct CSV file`() {
        // given
        val resource = ClassPathResource("conversion-rates/2020.csv")
        val expected = mapOf(
            LocalDate.of(2020, 1, 1) to mapOf(
                "JPY" to 106.92,
                "USD" to 0.9305,
                "EUR" to 1.0000,
            ),
            LocalDate.of(2020, 1, 2) to mapOf(
                "JPY" to 108.26,
                "USD" to 0.9423,
                "EUR" to 1.0000,
            ),
        )

        // when
        val result = conversionRatesExtractor.extractConversionRates(resource)

        // then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `should throw an expected exception extracting rates from the file containing incorrect date`() {
        // given
        val resource = ClassPathResource("conversion-rates/2021.csv")
        val expectedElementsInErrorMessage = arrayOf(
            "file: 2021.csv",
            "date: 2020-01-01",
            "line: 2",
        )

        // when
        val result = catchThrowable { conversionRatesExtractor.extractConversionRates(resource) }

        // then
        assertThat(result)
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContainingAll(*expectedElementsInErrorMessage)
    }
}
