package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("expense_items")
@EqualsAndHashCode(callSuper = true)
public class ExpenseItem extends DomainObject<Long> {
    private LocalDate date;
    private BigDecimal amount;
    private String currency;
    private String comment;
    private Long expenseId;
}
