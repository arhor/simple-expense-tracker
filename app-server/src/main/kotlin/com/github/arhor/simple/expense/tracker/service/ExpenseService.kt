package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.util.TemporalRange
import java.time.LocalDate

interface ExpenseService {

    fun getUserExpenses(userId: Long, dateRange: TemporalRange<LocalDate>): List<ExpenseResponseDTO>

    fun getExpenseById(expenseId: Long, dateRange: TemporalRange<LocalDate>): ExpenseResponseDTO

    fun createUserExpense(userId: Long, requestDTO: ExpenseRequestDTO): ExpenseResponseDTO
}
