package com.github.arhor.simple.expense.tracker.validation

import com.github.arhor.simple.expense.tracker.DateRangeCriteria
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream
import javax.validation.ConstraintValidatorContext

internal class DateRangeValidatorTest {

    @MethodSource("data")
    @ParameterizedTest
    fun `should return expected result for the given date range`(
        dateRange: DateRangeCriteria?,
        expectedResult: Boolean
    ) {
        // given
        val validator = DateRangeValidator()
        val context = mockk<ConstraintValidatorContext>()

        // when
        val result = validator.isValid(dateRange, context)

        // then
        assertThat(result)
            .isEqualTo(expectedResult)
    }

    companion object {
        private val TODAY = LocalDate.parse("2022-09-11")
        private val TOMORROW = LocalDate.parse("2022-09-12")

        @JvmStatic
        fun data(): Stream<Arguments> = Stream.of(
            arguments(null, true),
            arguments(DateRangeCriteria(startDate = null, endDate = null), true),
            arguments(DateRangeCriteria(startDate = null, endDate = TODAY), true),
            arguments(DateRangeCriteria(startDate = TODAY, endDate = null), true),
            arguments(DateRangeCriteria(startDate = TODAY, endDate = TODAY), true),
            arguments(DateRangeCriteria(startDate = TODAY, endDate = TOMORROW), true),
            arguments(DateRangeCriteria(startDate = TOMORROW, endDate = TODAY), false),
        )
    }
}
