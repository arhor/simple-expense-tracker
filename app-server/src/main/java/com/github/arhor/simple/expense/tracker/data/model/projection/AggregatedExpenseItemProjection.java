package com.github.arhor.simple.expense.tracker.data.model.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents {@link com.github.arhor.simple.expense.tracker.data.model.ExpenseItem} projection containing summarized
 * information for the given currency and expense date.
 *
 * @param date        expense date
 * @param currency    expense currency
 * @param totalAmount expense total amount for the given date and currency
 */
public record AggregatedExpenseItemProjection(
    LocalDate date,
    String currency,
    BigDecimal totalAmount
) {
}
