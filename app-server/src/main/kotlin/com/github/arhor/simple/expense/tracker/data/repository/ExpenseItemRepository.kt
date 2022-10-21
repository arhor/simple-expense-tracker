package com.github.arhor.simple.expense.tracker.data.repository;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseItemProjection
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.time.LocalDate
import java.util.stream.Stream

interface ExpenseItemRepository : CrudRepository<ExpenseItem, Long> {

    @Query(name = "ExpenseItem.findAllByExpenseIdAndDateRange")
    fun findAllByExpenseIdAndDateRange(
        expenseId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Stream<ExpenseItem>

    @Query(name = "ExpenseItem.findAllAggregatedByExpenseIdsAndDateRange")
    fun findAllAggregatedByExpenseIdsAndDateRange(
        expenseIds: Iterable<Long>,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<AggregatedExpenseItemProjection>
}
