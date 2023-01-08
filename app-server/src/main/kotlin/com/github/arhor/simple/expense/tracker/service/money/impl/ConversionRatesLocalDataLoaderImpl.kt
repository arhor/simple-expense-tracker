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

    override fun loadConversionRatesDataByYear(year: Int, consumer: (Map<LocalDate, Map<String, Double>>) -> Unit) {
        withLocalData { resources ->
            for (resource in resources) {
                val filename = resource.filename

                if ((filename != null) && filename.contains(year.toString())) {
                    consumer(conversionRatesExtractor.extractConversionRates(resource))
                    break
                }
            }
            logger.warn("Local data-files does not contain data for the year: {}", year)
        }
    }

    private fun withLocalData(consumer: (Array<Resource>) -> Unit) {
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
