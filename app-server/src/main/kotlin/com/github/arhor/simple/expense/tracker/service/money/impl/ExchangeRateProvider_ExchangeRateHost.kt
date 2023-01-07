package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesData
import jakarta.annotation.Priority
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import java.util.*
import javax.money.Monetary
import javax.money.convert.ConversionQuery
import javax.money.convert.ExchangeRate
import javax.money.convert.RateType

@Service
@Priority(2)
@Suppress("ClassName")
class ExchangeRateProvider_ExchangeRateHost(
    restTemplateBuilder: RestTemplateBuilder,
) : ExchangeRateProvider_Base(
    provider = "EXCHANGERATE_HOST",
    rateType = RateType.DEFERRED,
) {
    private val http = restTemplateBuilder.rootUri(EXCHANGERATE_HOST_URL).build()

    override fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate>? {
        val date = getQueryDates(conversionQuery).singleOrNull()?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            ?: "/latest"
        return http.getForObject(DATE, responseType, date)?.let {
            logger.info("Loaded rates for the: {}", it.date)
            save(Monetary.getCurrency(it.base), mapOf(it.date to it.rates))
            loadedRates[it.date]
        }
    }

    companion object {
        private const val EXCHANGERATE_HOST_URL = "https://api.exchangerate.host"
        private const val DATE = "/{date}"
        private val logger = LoggerFactory.getLogger(ExchangeRateProvider_ExchangeRateHost::class.java)
        private val responseType = ConversionRatesData::class.java
    }
}
