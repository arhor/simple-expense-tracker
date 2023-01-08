package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import jakarta.annotation.Priority
import org.springframework.stereotype.Service
import javax.money.convert.ConversionQuery
import javax.money.convert.ExchangeRate
import javax.money.convert.RateType

@Service
@Priority(1)
class ExchangeRateProviderLocalData(
    private val conversionRatesLocalDataLoader: ConversionRatesLocalDataLoader
) : ExchangeRateProviderBase(
    provider = PROVIDER_NAME,
    rateType = RateType.HISTORIC,
) {
    override fun findExchangeRates(conversionQuery: ConversionQuery): Map<String, ExchangeRate>? =
        getQueryDates(conversionQuery).singleOrNull()?.let { date ->
            var result = loadedRates[date]

            if (result == null) {
                conversionRatesLocalDataLoader.loadConversionRatesDataByYear(date.year) {
                    save(BASE_CURRENCY, it)
                }
                result = loadedRates[date]
            }
            return result
        }

    companion object {
        private const val PROVIDER_NAME = "LOCAL_DATA"
    }
}
