package com.github.arhor.simple.expense.tracker.service.money.impl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import jakarta.annotation.Priority
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.money.Monetary
import javax.money.convert.ConversionQuery
import javax.money.convert.ExchangeRate
import javax.money.convert.RateType

@Service
@Priority(2)
class ExchangeRateProviderExternalApi(
    applicationProps: ApplicationProps,
    restTemplateBuilder: RestTemplateBuilder,
) : ExchangeRateProviderBase(
    provider = PROVIDER_NAME,
    rateType = RateType.DEFERRED,
) {
    private val http = restTemplateBuilder.rootUri(applicationProps.conversionRates.apiPath).build()

    override fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate>? =
        http.getForObject(DATE, responseType, conversionQuery.dateUrlParam())?.let {
            logger.debug("Loaded rates for the: {}", it.date)
            save(Monetary.getCurrency(it.base), mapOf(it.date to it.rates))
            loadedRates[it.date]
        }

    private fun ConversionQuery.dateUrlParam(): String {
        return getQueryDates(this).singleOrNull()?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            ?: LATEST_DATE
    }

    companion object {
        private const val PROVIDER_NAME = "EXTERNAL_API"
        private const val LATEST_DATE = "latest"
        private const val DATE = "/{date}"
        private val logger = LoggerFactory.getLogger(ExchangeRateProviderExternalApi::class.java)
        private val responseType = ConversionRatesData::class.java

        @JsonIgnoreProperties(ignoreUnknown = true)
        private data class ConversionRatesData(
            val base: String,
            val date: LocalDate,
            val rates: Map<String, Double>,
        )
    }
}
