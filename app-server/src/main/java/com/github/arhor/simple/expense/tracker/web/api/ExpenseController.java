package com.github.arhor.simple.expense.tracker.web.api;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.arhor.simple.expense.tracker.model.ExpenseDTO;
import com.github.arhor.simple.expense.tracker.service.ExpenseService;
import com.github.arhor.simple.expense.tracker.service.UserService;

/**
 * Provides REST-API for the 'expenses' resource.
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    /**
     * Returns list of the current user expenses. Authentication required.
     *
     * @param auth current user authentication
     *
     * @return expenses list
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ExpenseDTO> getUserExpenses(final Authentication auth) {
        var currentUserId = userService.determineUserId(auth);
        return expenseService.getUserExpenses(currentUserId);
    }
}
