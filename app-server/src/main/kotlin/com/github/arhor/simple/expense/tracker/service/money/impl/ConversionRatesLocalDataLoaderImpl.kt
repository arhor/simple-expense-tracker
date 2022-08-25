package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesLocalDataLoader
import de.siegmar.fastcsv.reader.NamedCsvReader
import de.siegmar.fastcsv.reader.NamedCsvRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Service
import java.io.IOException
import java.lang.invoke.MethodHandles
import java.time.LocalDate
import java.time.chrono.IsoChronology

@Service
class ConversionRatesLocalDataLoaderImpl(
    private val applicationProps: ApplicationProps,
    private val resourcePatternResolver: ResourcePatternResolver,
) : ConversionRatesLocalDataLoader {

    override fun loadConversionRatesDataByYear(year: Int, consumer: (Map<LocalDate, Map<String, Double>>) -> Unit) {
        withLocalData { resources ->
            for (resource in resources) {
                val filename = resource.filename

                if ((filename != null) && filename.contains(year.toString())) {
                    readDataFile(resource, consumer)
                    break
                }
            }
            log.warn("Local data-files does not contain data for the year: {}", year)
        }
    }

    override fun loadInitialConversionRates(consumer: (Map<LocalDate, Map<String, Double>>) -> Unit) {
        applicationProps.conversionRates?.let { (_, preload) ->
            withLocalData {
                runBlocking(Dispatchers.IO) {
                    it.sortedBy { it.filename }.takeLast(preload).map { resource ->
                        launch {
                            try {
                                readDataFile(resource, consumer)
                            } catch (e: NumberFormatException) {
                                log.error(
                                    "Conversion-rates filename must represent the year for which it contains data",
                                    e
                                )
                            }
                        }
                    }.joinAll()
                }
            }
        }
    }

    private fun withLocalData(consumer: (Array<Resource>) -> Unit) {
        applicationProps.conversionRates?.let {
            try {
                consumer(
                    resourcePatternResolver.getResources(
                        it.pattern
                    )
                )
            } catch (e: IOException) {
                log.error("Failed to load conversion-rates from local data-files", e)
            }
        }
    }

    private fun readDataFile(
        resource: Resource,
        consumer: (Map<LocalDate, Map<String, Double>>) -> Unit
    ) {
        try {
            val year = resource.filename!!.replace(".csv", "").toLong()

            NamedCsvReader.builder().skipComments(true).build(resource.inputStream.reader()).use { csv ->
                try {
                    val length = determineMapCapacity(year)
                    val result = HashMap<LocalDate, Map<String, Double>>(length)

                    for (row in csv) {
                        handleCsvRow(row, result::put)
                    }
                    consumer(result)
                    log.info("[SUCCESS]: {} year conversion rates loaded", year)
                } catch (e: IOException) {
                    log.warn("Failed to load rates for the year: {}", year, e)
                }
            }
        } catch (e: NumberFormatException) {
            log.error(
                "Conversion-rates filename must represent the year for which it contains data",
                e
            )
        }
    }

    private fun handleCsvRow(csvRow: NamedCsvRow, consumer: (LocalDate, Map<String, Double>) -> Unit) {
        val currentRowFields = csvRow.fields
        val rates = HashMap<String, Double>(currentRowFields.size - 1)

        var date: LocalDate? = null

        for ((name, value) in currentRowFields.entries) {
            if (COL_DATE == name) {
                if (date == null) {
                    date = LocalDate.parse(value)
                } else {
                    throwDateColumnException(csvRow)
                }
            } else if (isPresent(value)) {
                rates[name] = value.toDouble()
            }
        }
        if (date == null) {
            throwDateColumnException(csvRow)
        }
        consumer(date, rates)
    }

    private fun isPresent(value: String?): Boolean {
        return !value.isNullOrBlank()
    }

    private fun determineMapCapacity(year: Long): Int {
        return when (IsoChronology.INSTANCE.isLeapYear(year)) {
            true -> 366
            else -> 365
        }
    }

    private fun throwDateColumnException(csvRow: NamedCsvRow): Nothing {
        throw IllegalStateException(
            "There must be exactly one column '$COL_DATE' in the row: $csvRow"
        )
    }

    companion object {
        private const val COL_DATE = "date"
        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
