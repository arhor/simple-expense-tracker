package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesDataHolder
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesExtractor
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class ConversionRatesLocalDataLoaderImpl(
    private val applicationProps: ApplicationProps,
    private val resourcePatternResolver: ResourcePatternResolver,
    private val conversionRatesExtractor: ConversionRatesExtractor,
) : ConversionRatesLocalDataLoader {

    override fun loadConversionRatesDataByYear(year: Int, consumer: (ConversionRatesDataHolder) -> Unit) {
        withLocalData { resources ->
            for (resource in resources) {
                val filename = resource.filename

                if ((filename != null) && filename.contains(year.toString())) {
                    consumer(conversionRatesExtractor.extractConversionRates(resource))
                    break
                }
            }
            log.warn("Local data-files does not contain data for the year: {}", year)
        }
    }

    override fun loadInitialConversionRates(consumer: (ConversionRatesDataHolder) -> Unit) {
        applicationProps.conversionRates?.let { (_, preload) ->
            withLocalData { resources ->
                runBlocking(Dispatchers.IO) {
                    resources.sortedBy { it.filename }.takeLast(preload ?: 0).map { resource ->
                        launch {
                            consumer(conversionRatesExtractor.extractConversionRates(resource))
                        }
                    }.joinAll()
                }
            }
        }
    }

    private fun withLocalData(consumer: (Array<Resource>) -> Unit) {
        applicationProps.conversionRates?.pattern?.let {
            try {
                consumer(
                    resourcePatternResolver.getResources(it)
                )
            } catch (e: IOException) {
                log.error("Failed to load conversion-rates from local data-files", e)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ConversionRatesLocalDataLoaderImpl::class.java)
    }
}
