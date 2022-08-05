package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection;

public interface ExpenseItemRepository extends CrudRepository<ExpenseItem, Long> {

    @Query(name = "ExpenseItem.findAllByExpenseIdAndDateRange")
    Stream<ExpenseItem> findAllByExpenseIdAndDateRange(
        Long expenseId,
        LocalDate startDate,
        LocalDate endDate
    );

    @Query(name = "ExpenseItem.findAllAggregatedByExpenseIdsAndDateRange")
    List<AggregatedExpenseItemProjection> findAllAggregatedByExpenseIdsAndDateRange(
        Iterable<Long> expenseIds,
        LocalDate startDate,
        LocalDate endDate
    );
}
