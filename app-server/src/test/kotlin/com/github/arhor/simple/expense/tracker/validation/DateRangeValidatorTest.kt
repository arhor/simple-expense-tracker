package com.github.arhor.simple.expense.tracker.validation

import com.github.arhor.simple.expense.tracker.DateRangeCriteria
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream

internal class DateRangeValidatorTest {

    private val validator = DateRangeValidator()

    @MethodSource
    @ParameterizedTest
    fun `should return expected result for the given date range`(
        // given
        dateRange: DateRangeCriteria?,
        expectedResult: Boolean
    ) {
        // when
        val result = validator.isValid(dateRange, null)

        // then
        assertThat(result)
            .isEqualTo(expectedResult)
    }

    companion object {
        private val DATE_1 = LocalDate.parse("2022-09-11")
        private val DATE_2 = LocalDate.parse("2022-09-12")

        @JvmStatic
        fun `should return expected result for the given date range`(): Stream<Arguments> = Stream.of(
            arguments(null, true),
            arguments(DateRangeCriteria(startDate = null, endDate = null), true),
            arguments(DateRangeCriteria(startDate = null, endDate = DATE_1), true),
            arguments(DateRangeCriteria(startDate = DATE_1, endDate = null), true),
            arguments(DateRangeCriteria(startDate = DATE_1, endDate = DATE_1), true),
            arguments(DateRangeCriteria(startDate = DATE_1, endDate = DATE_2), true),
            arguments(DateRangeCriteria(startDate = DATE_2, endDate = DATE_1), false),
        )
    }
}
