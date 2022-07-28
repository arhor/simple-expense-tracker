package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.util.List;

import com.github.arhor.simple.expense.tracker.model.ExpenseDetailsResponseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

public interface ExpenseService {

    List<ExpenseResponseDTO> getUserExpenses(Long userId, TemporalRange<LocalDate> dateRange);

    ExpenseDetailsResponseDTO getUserExpenseById(Long userId, Long expenseId, TemporalRange<LocalDate> dateRange);

    ExpenseResponseDTO createUserExpense(Long userId, ExpenseRequestDTO requestDTO);
}
