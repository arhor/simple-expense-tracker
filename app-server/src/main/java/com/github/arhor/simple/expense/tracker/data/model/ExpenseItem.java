package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Table;

@Table("expense_items")
@Builder(toBuilder = true)
@Immutable
public record ExpenseItem(
    @Id
    Long id,
    LocalDate date,
    BigDecimal amount,
    String currency,
    String comment,
    Long expenseId
) {
}
