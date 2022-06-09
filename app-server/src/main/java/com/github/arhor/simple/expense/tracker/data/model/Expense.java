package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("expenses")
@EqualsAndHashCode(callSuper = true)
public class Expense extends AuditableDomainObject<Long> {

    private Long userId;
    private String name;
    private String icon;
    private String color;
}
