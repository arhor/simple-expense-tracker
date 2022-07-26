package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    Stream<Expense> findAllByUserId(Long userId);
}
