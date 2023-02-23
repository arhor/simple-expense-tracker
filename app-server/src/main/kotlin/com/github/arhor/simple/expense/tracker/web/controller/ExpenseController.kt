package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.ExpenseItemService
import com.github.arhor.simple.expense.tracker.service.ExpenseService
import com.github.arhor.simple.expense.tracker.service.TimeService
import com.github.arhor.simple.expense.tracker.web.DateRangeCriteria
import com.github.arhor.simple.expense.tracker.web.validation.ValidDateRange
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.TimeZone

/**
 * Provides REST-API for the 'expenses' resource.
 */
@Validated
@RestController
@RequestMapping("/expenses")
@PreAuthorize("isAuthenticated()")
@SecurityRequirements(
    value = [
        SecurityRequirement(name = "authenticated", scopes = ["USER"])
    ]
)
class ExpenseController(
    private val expenseItemService: ExpenseItemService,
    private val expenseService: ExpenseService,
    private val timeService: TimeService,
) {

    @Operation(
        summary = "Current user expenses",
        description = "Returns list of the current user expenses",
    )
    @GetMapping
    fun getCurrentUserExpenses(
        @ValidDateRange criteria: DateRangeCriteria,
        @AuthenticationPrincipal currentUser: CustomUserDetails,
        timezone: TimeZone,
    ): List<ExpenseResponseDTO> {
        val dateRange = timeService.convertToDateRange(criteria.startDate, criteria.endDate, timezone)

        return expenseService.getUserExpenses(currentUser.id!!, dateRange)
    }

    @Operation(
        summary = "User expense by id",
        description = "Returns user expense info by its id",
    )
    @GetMapping("/{expenseId}")
    fun getExpenseById(
        @PathVariable expenseId: Long,
        @ValidDateRange criteria: DateRangeCriteria,
        timezone: TimeZone,
    ): ExpenseResponseDTO {
        val dateRange = timeService.convertToDateRange(criteria.startDate, criteria.endDate, timezone)

        return expenseService.getExpenseById(expenseId, dateRange)
    }

    @Operation(
        summary = "Create new user expense",
        description = "Creates new expense for the currently authenticated user",
    )
    @PostMapping
    fun createCurrentUserExpense(
        @RequestBody requestBody: ExpenseRequestDTO,
        @AuthenticationPrincipal currentUser: CustomUserDetails,
    ): ResponseEntity<ExpenseResponseDTO> {
        val createdExpense = expenseService.createUserExpense(currentUser.id!!, requestBody)

        val location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{expenseId}")
                .build(createdExpense.id)

        return ResponseEntity.created(location).body(createdExpense)
    }

    @Operation(
        summary = "Create new user expense item",
        description = "Creates new expense item for the currently authenticated user",
    )
    @PostMapping("/{expenseId}/items")
    fun createUserExpenseItem(
        @PathVariable expenseId: Long,
        @RequestBody requestBody: ExpenseItemRequestDTO,
        @AuthenticationPrincipal currentUser: CustomUserDetails,
    ): ResponseEntity<ExpenseItemResponseDTO> {
        val createdExpenseItem = expenseItemService.createExpenseItem(currentUser.id!!, expenseId, requestBody)

        val location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{expenseItemId}")
                .build(createdExpenseItem.id)

        return ResponseEntity.created(location).body(createdExpenseItem)
    }

    @Operation(
        summary = "Expense items associated with an expense",
        description = "Returns expense items by associated expense id",
    )
    @GetMapping("/{expenseId}/items")
    fun getUserExpenseItems(
        @PathVariable expenseId: Long,
        @ValidDateRange criteria: DateRangeCriteria,
        timezone: TimeZone,
    ): List<ExpenseItemResponseDTO> {
        return expenseItemService.getExpenseItems(
            expenseId,
            timeService.convertToDateRange(
                criteria.startDate,
                criteria.endDate,
                timezone
            )
        )
    }
}
