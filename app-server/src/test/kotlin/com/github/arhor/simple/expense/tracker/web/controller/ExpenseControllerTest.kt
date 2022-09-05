package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.DateRangeCriteria
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService
import com.github.arhor.simple.expense.tracker.service.ExpenseService
import com.github.arhor.simple.expense.tracker.service.TimeService
import com.github.arhor.simple.expense.tracker.service.UserService
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.get
import java.math.BigDecimal
import java.time.LocalDate
import java.util.TimeZone

@WebMvcTest(ExpenseController::class)
internal class ExpenseControllerTest : BaseControllerTest() {

    @Autowired
    private lateinit var applicationProps: ApplicationProps

    @MockkBean
    private lateinit var expenseService: ExpenseService

    @MockkBean
    private lateinit var expenseItemService: ExpenseItemService

    @MockkBean
    private lateinit var timeService: TimeService

    @MockkBean
    private lateinit var userService: UserService

    @Test
    @WithMockUser
    fun `should return status 200 and expense list with expected content`() {
        // given
        val expensesEndPoint = applicationProps.apiUrlPath("/expenses")

        val authentication = slot<Authentication>()

        val dateRangeStart = LocalDate.of(2022, 8, 1)
        val dateRangeEnd = LocalDate.of(2022, 8, 31)

        val expectedUserId = -1L
        val expectedCriteria = DateRangeCriteria(dateRangeStart, dateRangeEnd)
        val expectedTimeZone = TimeZone.getDefault()
        val expectedDateRange = TemporalRange(dateRangeStart, dateRangeEnd)
        val expectedExpenseId = 1L
        val expectedExpenseName = "test-name"
        val expectedExpenseIcon = "test-icon"
        val expectedExpenseColor = "test-color"
        val expectedExpenseTotal = BigDecimal.TEN

        val expectedExpenses = listOf(
            ExpenseResponseDTO(
                expectedExpenseId,
                expectedExpenseName,
                expectedExpenseIcon,
                expectedExpenseColor,
                expectedExpenseTotal,
            )
        )

        every { userService.determineUserId(auth = any()) } returns expectedUserId
        every { timeService.convertToDateRange(criteria = any(), timezone = any()) } returns expectedDateRange
        every { expenseService.getUserExpenses(userId = any(), dateRange = any()) } returns expectedExpenses

        // when
        val result = http.get(expensesEndPoint) {
            param("startDate", dateRangeStart.toString())
            param("endDate", dateRangeEnd.toString())
        }

        // then
        verify(exactly = 1) { userService.determineUserId(capture(lst = authentication)) }
        verify(exactly = 1) { timeService.convertToDateRange(criteria = expectedCriteria, timezone = expectedTimeZone) }
        verify(exactly = 1) { expenseService.getUserExpenses(userId = expectedUserId, dateRange = expectedDateRange) }

        assertThat(authentication.captured)
            .isNotNull
            .satisfies(authenticatedUser)

        result.andExpect {
            status { isOk() }
            content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
            jsonPath("$") { isArray() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$.[0].id") { value(expectedExpenseId) }
            jsonPath("$.[0].name") { value(expectedExpenseName) }
            jsonPath("$.[0].icon") { value(expectedExpenseIcon) }
            jsonPath("$.[0].color") { value(expectedExpenseColor) }
            jsonPath("$.[0].total") { value(expectedExpenseTotal) }
        }
    }
}
