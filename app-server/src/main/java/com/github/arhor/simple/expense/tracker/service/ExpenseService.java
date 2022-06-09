package com.github.arhor.simple.expense.tracker.service;

import java.util.List;

import com.github.arhor.simple.expense.tracker.model.ExpenseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;

public interface ExpenseService {

    List<ExpenseDTO> getUserExpenses(Long userId);

    ExpenseDTO createUserExpense(Long userId, ExpenseDTO dto);

    ExpenseItemDTO createExpenseItem(Long expenseId, ExpenseItemDTO dto);
}
