package com.github.arhor.simple.expense.tracker.data.model.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents [com.github.arhor.simple.expense.tracker.data.model.ExpenseItem] projection containing summarized
 * information for the given currency and expense date.
 *
 * @param expenseId   expense id
 * @param date        expense date
 * @param currency    expense currency
 * @param totalAmount expense total amount for the given date and currency
 */
data class AggregatedExpenseItemProjection(
    val expenseId: Long,
    val date: LocalDate,
    val currency: String,
    val totalAmount: BigDecimal,
)
