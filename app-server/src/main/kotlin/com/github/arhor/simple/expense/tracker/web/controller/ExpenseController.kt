package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService
import com.github.arhor.simple.expense.tracker.service.ExpenseService
import com.github.arhor.simple.expense.tracker.service.TimeService
import com.github.arhor.simple.expense.tracker.service.UserService
import com.github.arhor.simple.expense.tracker.validation.ValidDateRange
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

/**
 * Provides REST-API for the 'expenses' resource.
 */
@Validated
@RestController
@RequestMapping("/expenses")
@PreAuthorize("isAuthenticated()")
class ExpenseController(
    private val expenseItemService: ExpenseItemService,
    private val expenseService: ExpenseService,
    private val timeService: TimeService,
    private val userService: UserService,
) {

    /**
     * Returns list of the current user expenses. Authentication required.
     *
     * @param auth current user authentication
     *
     * @return expenses list
     */
    @GetMapping
    fun getUserExpenses(
        @ValidDateRange criteria: DateRangeCriteria,
        timezone: TimeZone,
        auth: Authentication,
    ): List<ExpenseResponseDTO> {
        val currentUserId = userService.determineUserId(auth)
        val dateRange = timeService.convertToDateRange(criteria, timezone)

        return expenseService.getUserExpenses(currentUserId, dateRange)
    }

    @GetMapping("/{expenseId}")
    fun getExpenseById(
        @PathVariable expenseId: Long,
        @ValidDateRange criteria: DateRangeCriteria,
        timezone: TimeZone,
    ): ExpenseResponseDTO {
        val dateRange = timeService.convertToDateRange(criteria, timezone)

        return expenseService.getExpenseById(expenseId, dateRange)
    }

    @PostMapping
    fun createUserExpense(
        @RequestBody requestDTO: ExpenseRequestDTO,
        auth: Authentication,
    ): ResponseEntity<ExpenseResponseDTO> {
        val currentUserId = userService.determineUserId(auth)
        val createdExpense = expenseService.createUserExpense(currentUserId, requestDTO)

        val location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{expenseId}")
                .build(createdExpense.id)

        return ResponseEntity.created(location).body(createdExpense)
    }

    @PostMapping("/{expenseId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUserExpenseItem(
        @PathVariable expenseId: Long,
        @RequestBody dto: ExpenseItemDTO,
    ): ExpenseItemDTO {
        return expenseItemService.createExpenseItem(expenseId, dto)
    }

    @GetMapping("/{expenseId}/items")
    fun getUserExpenseItems(
        @PathVariable expenseId: Long,
        @ValidDateRange criteria: DateRangeCriteria?,
        timezone: TimeZone?,
    ): List<ExpenseItemDTO> {
        return expenseItemService.getExpenseItems(
            expenseId,
            timeService.convertToDateRange(
                criteria,
                timezone
            )
        )
    }
}
