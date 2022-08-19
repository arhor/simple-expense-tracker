package com.github.arhor.simple.expense.tracker.data.repository;

import reactor.core.publisher.Flux;

import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection;

public interface ExpenseItemRepository extends ReactiveCrudRepository<ExpenseItem, Long> {

    // language=SQL
    @Query("""
        SELECT ei.date
             , ei.amount
             , ei.currency
          FROM expense_items ei
         WHERE ei.expense_id = :expenseId
           AND ei.date BETWEEN :startDate AND :endDate
        """)
    Flux<ExpenseItem> findAllByExpenseIdAndDateRange(
        Long expenseId,
        LocalDate startDate,
        LocalDate endDate
    );

    // language=SQL
    @Query("""
          SELECT ei.expense_id
               , ei.date
               , ei.currency
               , SUM(ei.amount) AS "total_amount"
            FROM expense_items ei
        GROUP BY ei.expense_id
               , ei.date
               , ei.currency
          HAVING ei.expense_id IN (:expenseIds)
             AND ei.date BETWEEN :startDate AND :endDate
        ORDER BY ei.expense_id
        """)
    Flux<AggregatedExpenseItemProjection> findAllAggregatedByExpenseIdsAndDateRange(
        Iterable<Long> expenseIds,
        LocalDate startDate,
        LocalDate endDate
    );
}
