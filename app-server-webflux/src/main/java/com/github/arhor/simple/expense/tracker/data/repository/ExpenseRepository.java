package com.github.arhor.simple.expense.tracker.data.repository;

import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

public interface ExpenseRepository extends ReactiveCrudRepository<Expense, Long> {

    Flux<Expense> findAllByUserId(Long userId);
}
