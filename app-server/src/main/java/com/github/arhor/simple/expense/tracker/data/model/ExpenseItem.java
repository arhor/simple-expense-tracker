package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("expense_items")
@EqualsAndHashCode(callSuper = true)
public class ExpenseItem extends DomainObject<Long> {

    private Long expenseId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String comment;
}
