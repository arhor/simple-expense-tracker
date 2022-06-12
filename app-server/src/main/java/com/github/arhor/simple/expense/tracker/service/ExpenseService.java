package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.util.List;

import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;
import com.github.arhor.simple.expense.tracker.service.TimeService.TemporalRange;

public interface ExpenseService {

    List<ExpenseResponseDTO> getUserExpenses(Long userId, TemporalRange<LocalDate> dateRange);

    ExpenseResponseDTO getUserExpenseById(Long userId, Long expenseId, TemporalRange<LocalDate> dateRange);

    ExpenseResponseDTO createUserExpense(Long userId, ExpenseRequestDTO requestDTO);

    ExpenseItemDTO createExpenseItem(Long expenseId, ExpenseItemDTO dto);
}
