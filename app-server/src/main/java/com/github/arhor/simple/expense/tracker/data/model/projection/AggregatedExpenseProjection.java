package com.github.arhor.simple.expense.tracker.data.model.projection;

import java.util.List;

/**
 * Represents {@link com.github.arhor.simple.expense.tracker.data.model.Expense} projection with minimal required field
 * set and list expense items grouped by date and currency.
 *
 * @param id     expense id
 * @param userId expense user id
 * @param name   expense name
 * @param icon   expense icon name
 * @param color  expense color name
 * @param items  expense aggregated items
 */
public record AggregatedExpenseProjection(
    Long id,
    Long userId,
    String name,
    String icon,
    String color,
    List<AggregatedExpenseItemProjection> items
) {
}
