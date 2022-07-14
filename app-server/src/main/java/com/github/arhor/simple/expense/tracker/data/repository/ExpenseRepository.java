package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

public interface ExpenseRepository extends BaseRepository<Expense, Long> {

    @Query("SELECT e.* FROM expenses e WHERE e.deleted = FALSE AND e.user_id = :userId")
    Stream<Expense> findByUserId(Long userId);

    @Query("SELECT e.* FROM expenses e WHERE e.deleted = FALSE AND e.user_id = :userId AND e.id = :expenseId")
    Optional<Expense> findByUserIdAndExpenseId(Long userId, Long expenseId);

    @Override
    @Modifying
    @Query("UPDATE expenses SET deleted = TRUE WHERE id IN (:ids)")
    void deleteAllById(Iterable<? extends Long> ids);

    @Override
    @Modifying
    @Query("UPDATE expenses SET deleted = TRUE WHERE id = :id")
    void deleteById(Long id);

    @Override
    @Query("SELECT e.* FROM expenses e WHERE e.deleted = FALSE")
    List<Expense> findAll();

    @Query("SELECT e.* FROM expenses e WHERE e.deleted = FALSE LIMIT :size OFFSET :#{#page * #size}")
    List<Expense> findAll(int page, int size);

    @Override
    @Query("SELECT e.* FROM expenses e WHERE e.id IN (:ids) AND e.deleted = FALSE")
    List<Expense> findAllById(Iterable<Long> ids);

    @Override
    @Query("SELECT e.* FROM expenses e WHERE e.id = :id AND e.deleted = FALSE")
    Optional<Expense> findById(Long id);

    @Override
    @Query("SELECT COUNT(e) FROM expenses e WHERE e.deleted = FALSE")
    long count();

    @Override
    @Query("""
        SELECT
            CASE WHEN COUNT(e) > 0
                THEN TRUE
                ELSE FALSE
            END
        FROM expenses e
        WHERE e.deleted = FALSE
          AND e.id = :id""")
    boolean existsById(Long id);

    @Override
    @Modifying
    @Query("UPDATE expenses SET deleted = TRUE WHERE deleted = FALSE")
    void deleteAll();
}
