package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;

public interface ExpenseItemRepository extends BaseRepository<ExpenseItem, Long> {

    @Query("SELECT ei.* FROM expense_items ei WHERE ei.expense_id = :expenseId")
    List<ExpenseItem> findAllByExpenseId(Long expenseId);

    @Query("""
        SELECT ei.*
        FROM expense_items ei
        WHERE ei.expense_id = :expenseId
          AND ei.date BETWEEN :startDate AND :endDate""")
    Stream<ExpenseItem> findByExpenseIdAndDateRange(Long expenseId, LocalDate startDate, LocalDate endDate);
}
