package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import jakarta.annotation.Priority
import org.springframework.stereotype.Service
import java.util.*
import javax.money.convert.ConversionQuery
import javax.money.convert.ExchangeRate
import javax.money.convert.RateType

@Service
@Priority(1)
@Suppress("ClassName")
class ExchangeRateProvider_LocalData(
    private val conversionRatesLocalDataLoader: ConversionRatesLocalDataLoader
) : ExchangeRateProvider_Base(
    provider = "LOCAL_DATA",
    rateType = RateType.HISTORIC,
) {
    override fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate>? =
        getQueryDates(conversionQuery).singleOrNull()?.let { date ->
            var result = loadedRates[date]

            if (result == null) {
                conversionRatesLocalDataLoader.loadConversionRatesDataByYear(date.year) {
                    save(BASE_CURRENCY, it.data)
                }
                result = loadedRates[date]
            }
            return result
        }
}
