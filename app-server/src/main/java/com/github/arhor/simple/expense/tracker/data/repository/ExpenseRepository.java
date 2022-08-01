package com.github.arhor.simple.expense.tracker.data.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.data.model.projection.AggregatedExpenseProjection;
import com.github.arhor.simple.expense.tracker.data.repository.support.AggregatedExpenseExtractor;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    Stream<Expense> findAllByUserId(Long userId);

    @Query(name = "Expense.findAllByUserIdAndDateRange", resultSetExtractorRef = AggregatedExpenseExtractor.BEAN_NAME)
    List<AggregatedExpenseProjection> findAllByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);
}
