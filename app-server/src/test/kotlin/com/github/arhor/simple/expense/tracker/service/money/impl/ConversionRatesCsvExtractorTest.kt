package com.github.arhor.simple.expense.tracker.service.money.impl

import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

internal class ConversionRatesCsvExtractorTest {

    private val conversionRatesExtractor = ConversionRatesCsvExtractor()

    @Test
    fun `should pass 1`() {
        // given
        val resource = ClassPathResource("conversion-rates/2020.csv")

        // when
        val result = conversionRatesExtractor.extractConversionRates(resource)

        // then
        println(result)
    }

    @Test
    fun `should pass 2`() {
        // given
        val resource = ClassPathResource("conversion-rates/2021.csv")

        // when
        val result = conversionRatesExtractor.extractConversionRates(resource)

        // then
        println(result)
    }
}
