package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import java.time.LocalDate

interface ExpenseItemService {

    fun getExpenseItems(expenseId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseItemDTO>

    fun createExpenseItem(expenseId: Long, dto: ExpenseItemDTO): ExpenseItemDTO
}
