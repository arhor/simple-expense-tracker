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
        log.debug("Start extracting conversion rates from '{}'", resource.filename)

        val year = try {
            resource.filename!!.replace(FILE_EXT, EMPTY_STRING).toInt()
        } catch (e: NumberFormatException) {
            log.error("Conversion-rates filename must represent the year for which it contains data", e)
            throw e
        }

        NamedCsvReader.builder().skipComments(true).build(resource.inputStream.reader()).use { csv ->
            try {
                val conversionRates = ConversionRatesDataHolder(
                    data = readConversionRatesFromCsv(
                        year = year,
                        file = csv,
                        name = resource.filename,
                    )
                )
                log.debug("{} year conversion rates loaded", year)
                return conversionRates
            } catch (e: IOException) {
                log.error("{} year conversion rates cannot be loaded", year, e)
                throw e
            }
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
            extractRatesFromCsvRow(row) { date, rates ->
                if (date.year != year) {
                    throw IllegalStateException(
                        ERROR_UNEXPECTED_DATE_TEMPLATE.format(
                            name,
                            date,
                            row.originalLineNumber,
                            year,
                        )
                    )
                }
                result[date] = rates
            }
        }
        return result
    }

    private inline fun extractRatesFromCsvRow(csvRow: NamedCsvRow, action: (LocalDate, Map<String, Double>) -> Unit) {
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
            } else {
                if (!value.isNullOrBlank()) {
                    rates[name] = value.toDouble()
                } else {
                    log.debug("Missing conversion rate: {} - {}", date, name)
                }
            }
        }
        if (date == null) {
            throwDateColumnException(csvRow)
        }
        action(date, rates)
    }

    private fun determineMapCapacity(year: Int): Int {
        return when (IsoChronology.INSTANCE.isLeapYear(year.toLong())) {
            true -> 366
            else -> 365
        }
    }

    private fun throwDateColumnException(csvRow: NamedCsvRow): Nothing {
        throw IllegalStateException(
            ERROR_MULTIPLE_DATE_COLUMNS_TEMPLATE.format(
                COL_DATE,
                csvRow,
            )
        )
    }

    companion object {
        private const val FILE_EXT = ".csv"
        private const val COL_DATE = "date"
        private const val EMPTY_STRING = ""

        private const val ERROR_MULTIPLE_DATE_COLUMNS_TEMPLATE =
            "There must be exactly one column '%s' in the row: %s"
        private const val ERROR_UNEXPECTED_DATE_TEMPLATE =
            "Invalid date found in the file: %s, date: %s, line: %s - file should contain dates for the %s year only"

        private val log = LoggerFactory.getLogger(ConversionRatesCsvExtractor::class.java)
    }
}
