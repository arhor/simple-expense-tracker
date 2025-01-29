package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import com.github.arhor.simple.expense.tracker.service.util.TemporalRange
import java.time.LocalDate

interface ExpenseItemService {

    fun getExpenseItems(expenseId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseItemResponseDTO>

    fun createExpenseItem(userId: Long, expenseId: Long, dto: ExpenseItemRequestDTO): ExpenseItemResponseDTO
}
