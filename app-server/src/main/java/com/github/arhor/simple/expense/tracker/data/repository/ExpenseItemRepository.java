package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;

public interface ExpenseItemRepository extends CrudRepository<ExpenseItem, Long> {

    List<ExpenseItem> findAllByExpenseId(Long expenseId);

    @Query("""
        SELECT ei.*
        FROM expense_items ei
        WHERE ei.expense_id = :expenseId
          AND ei.date BETWEEN :startDate AND :endDate""")
    Stream<ExpenseItem> findAllByExpenseIdAndDateRange(Long expenseId, LocalDate startDate, LocalDate endDate);
}
