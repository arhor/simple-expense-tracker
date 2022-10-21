package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.DateRangeCriteria
import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.LocalDate
import java.util.TimeZone
import java.util.function.Consumer

@Suppress("ClassName")
@SpringJUnitConfig(TimeServiceImpl::class)
internal class TimeServiceTest {

    @Autowired
    private lateinit var timeService: TimeService

    @Nested
    inner class `TimeService # now` {
        @Test
        fun `should return non null date time with default timezone`() {
            // when
            val result = timeService.now()

            // then
            assertThat(result)
                .isNotNull
        }

        @Test
        fun `should return non null date time with provided null timezone`() {
            // given
            val timezone: TimeZone? = null

            // when
            val result = timeService.now(timezone)

            // then
            assertThat(result)
                .isNotNull
        }

        @Test
        fun `should return non null date time with provided non null timezone`() {
            // given
            val timezone = TimeZone.getTimeZone("PST")

            // when
            val result = timeService.now(timezone)

            // then
            assertThat(result)
                .isNotNull
        }
    }

    @Nested
    inner class `TimeService # convertToDateRange` {
        @Test
        fun `should return non null date range for non null criteria and timezone`() {
            // given
            val today = LocalDate.now()
            val start = today.minusWeeks(1)
            val criteria = DateRangeCriteria(start, today)
            val timezone = TimeZone.getTimeZone("PST")

            // when
            val result = timeService.convertToDateRange(criteria, timezone)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("dateRange.start")
                            .isNotNull()
                    },
                    {
                        assertThat(it.end)
                            .describedAs("dateRange.end")
                            .isNotNull()
                    }
                )
        }

        @Test
        fun `should return non null date range for null criteria and timezone`() {
            // given
            val criteria: DateRangeCriteria? = null
            val timezone: TimeZone? = null

            // when
            val result = timeService.convertToDateRange(criteria, timezone)

            // then
            assertThat(result)
                .isNotNull
                .satisfies(
                    Consumer {
                        assertThat(it.start)
                            .describedAs("dateRange.start")
                            .isNotNull()
                    },
                    {
                        assertThat(it.end)
                            .describedAs("dateRange.end")
                            .isNotNull()
                    }
                )
        }
    }
}
