package com.github.arhor.simple.expense.tracker.service.money.impl

import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesDataHolder
import com.github.arhor.simple.expense.tracker.service.money.ConversionRatesExtractor
import de.siegmar.fastcsv.reader.NamedCsvReader
import de.siegmar.fastcsv.reader.NamedCsvRow
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDate
import java.time.chrono.IsoChronology

@Component
class ConversionRatesCsvExtractor : ConversionRatesExtractor {

    override fun extractConversionRates(resource: Resource): ConversionRatesDataHolder {
        try {
            log.debug("Start extracting conversion rates from '{}'", resource.filename)

            val year = resource.filename!!.replace(FILE_EXT, EMPTY_STRING).toInt()

            NamedCsvReader.builder().skipComments(true).build(resource.inputStream.reader()).use { csv ->
                try {
                    val conversionRates = ConversionRatesDataHolder(
                        data = readConversionRatesFromCsv(
                            year = year,
                            file = csv,
                            name = resource.filename,
                        )
                    )
                    log.info("[SUCCESS]: {} year conversion rates loaded", year)
                    return conversionRates
                } catch (e: IOException) {
                    log.error("[FAILURE]: {} year conversion rates cannot be loaded", year, e)
                    throw e
                }
            }
        } catch (e: NumberFormatException) {
            log.error("[FAILURE]: Conversion-rates filename must represent the year for which it contains data", e)
            throw e
        }
    }

    private fun readConversionRatesFromCsv(
        year: Int,
        file: NamedCsvReader,
        name: String?
    ): HashMap<LocalDate, Map<String, Double>> {
        val length = determineMapCapacity(year)
        val result = HashMap<LocalDate, Map<String, Double>>(length)

        for (row in file) {
            val (date, rates) = readCsvRow(row)

            if (date.year != year) {
                throw IllegalStateException(
                    "Invalid date found parsing %s file, date: %s, row: %s".format(
                        name,
                        date,
                        row.originalLineNumber,
                    )
                )
            }
            result[date] = rates
        }
        return result
    }

    private fun readCsvRow(csvRow: NamedCsvRow): Pair<LocalDate, Map<String, Double>> {
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
        return date to rates
    }

    private fun determineMapCapacity(year: Int): Int {
        return when (IsoChronology.INSTANCE.isLeapYear(year.toLong())) {
            true -> 366
            else -> 365
        }
    }

    private fun isPresent(value: String?): Boolean {
        return !value.isNullOrBlank()
    }

    private fun throwDateColumnException(csvRow: NamedCsvRow): Nothing {
        throw IllegalStateException(
            "There must be exactly one column '${COL_DATE}' in the row: $csvRow"
        )
    }

    companion object {
        private const val FILE_EXT = ".csv"
        private const val COL_DATE = "date"
        private const val EMPTY_STRING = ""
        private val log = LoggerFactory.getLogger(ConversionRatesCsvExtractor::class.java)
    }
}
