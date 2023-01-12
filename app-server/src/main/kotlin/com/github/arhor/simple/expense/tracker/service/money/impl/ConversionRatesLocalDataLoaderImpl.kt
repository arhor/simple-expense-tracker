package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesExtractor
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDate

@Service
class ConversionRatesLocalDataLoaderImpl(
    private val applicationProps: ApplicationProps,
    private val resourcePatternResolver: ResourcePatternResolver,
    private val conversionRatesExtractor: ConversionRatesExtractor,
) : ConversionRatesLocalDataLoader {

    override fun loadConversionRatesDataByYear(year: Int): Map<LocalDate, Map<String, Double>> {
        withLocalData { resources ->
            for (resource in resources) {
                if (resource.filename?.contains(year.toString()) == true) {
                    return@withLocalData conversionRatesExtractor.extractConversionRates(resource)
                }
            }
            logger.warn("Local data-files does not contain data for the year: {}", year)
        }
        return emptyMap()
    }

    private inline fun <T> withLocalData(consumer: (Array<Resource>) -> T) {
        applicationProps.conversionRates.pattern.let {
            try {
                consumer(
                    resourcePatternResolver.getResources(it)
                )
            } catch (e: IOException) {
                logger.error("Failed to load conversion-rates from local data-files", e)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConversionRatesLocalDataLoaderImpl::class.java)
    }
}
