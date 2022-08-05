package com.github.arhor.simple.expense.tracker.service;

import java.time.LocalDate;
import java.util.List;

import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;
import com.github.arhor.simple.expense.tracker.util.TemporalRange;

public interface ExpenseItemService {

    List<ExpenseItemDTO> getExpenseItems(Long expenseId, TemporalRange<LocalDate> dateRange);

    ExpenseItemDTO createExpenseItem(Long expenseId, ExpenseItemDTO dto);
}
