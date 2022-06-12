package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;

@Repository
public interface ExpenseItemRepository extends BaseRepository<ExpenseItem, Long> {

    @Query("SELECT ei.* FROM expense_items ei WHERE ei.expense_id = :expenseId")
    List<ExpenseItem> findAllByExpenseId(Long expenseId);

    @Query("""
        SELECT ei.*
        FROM expense_items ei
        WHERE ei.expense_id = :expenseId
          AND ei.date BETWEEN :startDate AND :endDate""")
    List<ExpenseItem> findByExpenseIdAndDateRange(Long expenseId, LocalDate startDate, LocalDate endDate);
}
