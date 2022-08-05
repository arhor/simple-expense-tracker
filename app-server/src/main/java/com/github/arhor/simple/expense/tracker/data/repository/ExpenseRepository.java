package com.github.arhor.simple.expense.tracker.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.github.arhor.simple.expense.tracker.data.model.Expense;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    List<Expense> findAllByUserId(Long userId);
}
