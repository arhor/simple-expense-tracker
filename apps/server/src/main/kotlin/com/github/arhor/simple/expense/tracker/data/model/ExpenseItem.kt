package com.github.arhor.simple.expense.tracker.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate

@Immutable
@Table("expense_items")
data class ExpenseItem(
    @Id
    val id: Long? = null,
    val date: LocalDate,
    val amount: BigDecimal,
    val currency: String,
    val comment: String? = null,
    val expenseId: Long,
)
