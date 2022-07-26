package com.github.arhor.simple.expense.tracker.web.api;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.arhor.simple.expense.tracker.model.ExpenseDetailsResponseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.TimeService;
import com.github.arhor.simple.expense.tracker.service.UserService;
import com.github.arhor.simple.expense.tracker.web.validation.ValidDateRange;

/**
 * Provides REST-API for the 'expenses' resource.
 */
@Validated
@RestController
@RequestMapping("/expenses")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseController {

    private final ExpenseService expenseService;
    private final TimeService timeService;
    private final UserService userService;

    /**
     * Returns list of the current user expenses. Authentication required.
     *
     * @param auth current user authentication
     *
     * @return expenses list
     */
    @GetMapping
    public List<ExpenseResponseDTO> getUserExpenses(
        @ValidDateRange final DateRangeCriteria criteria,
        final TimeZone timezone,
        final Authentication auth
    ) {
        var currentUserId = userService.determineUserId(auth);
        var dateRange = timeService.convertToDateRange(criteria, timezone);

        return expenseService.getUserExpenses(currentUserId, dateRange);
    }

    @GetMapping("/{expenseId}")
    public ExpenseDetailsResponseDTO getExpenseById(
        @PathVariable final Long expenseId,
        @ValidDateRange final DateRangeCriteria criteria,
        final TimeZone timezone,
        final Authentication auth
    ) {
        var currentUserId = userService.determineUserId(auth);
        var dateRange = timeService.convertToDateRange(criteria, timezone);

        return expenseService.getUserExpenseById(currentUserId, expenseId, dateRange);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createUserExpense(
        @RequestBody final ExpenseRequestDTO requestDTO,
        final Authentication auth
    ) {
        var currentUserId = userService.determineUserId(auth);
        var createdExpense = expenseService.createUserExpense(currentUserId, requestDTO);

        var location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{expenseId}")
                .build(createdExpense.getId());

        return ResponseEntity.created(location).body(createdExpense);
    }

    @PostMapping("/{expenseId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseItemDTO createUserExpenseItem(
        @PathVariable final Long expenseId,
        @RequestBody final ExpenseItemDTO expenseItemDTO
    ) {
        return expenseService.createExpenseItem(expenseId, expenseItemDTO);
    }
}
