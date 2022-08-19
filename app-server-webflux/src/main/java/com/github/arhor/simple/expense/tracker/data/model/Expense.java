package com.github.arhor.simple.expense.tracker.data.model;

import lombok.Builder;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Table("expenses")
@Builder(toBuilder = true)
@Immutable
public record Expense(
    @Id
    Long id,
    Long userId,
    String name,
    String icon,
    String color,
    @CreatedDate
    LocalDateTime created,
    @LastModifiedDate
    LocalDateTime updated
) {
}
