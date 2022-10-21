package com.github.arhor.simple.expense.tracker.data.model.projection

/**
 * Represents [com.github.arhor.simple.expense.tracker.data.model.Expense] projection with minimal required field
 * set and list expense items grouped by date and currency.
 *
 * @param id     expense id
 * @param userId expense user id
 * @param name   expense name
 * @param icon   expense icon name
 * @param color  expense color name
 * @param items  expense aggregated items
 */
data class AggregatedExpenseProjection(
    val id: Long,
    val userId: Long,
    val name: String,
    val icon: String?,
    val color: String?,
    val items: List<AggregatedExpenseItemProjection>,
)
