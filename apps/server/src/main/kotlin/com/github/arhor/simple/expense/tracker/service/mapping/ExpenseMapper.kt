package com.github.arhor.simple.expense.tracker.service.mapping;

import com.github.arhor.simple.expense.tracker.data.model.Expense
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO

interface ExpenseMapper {
    fun mapToEntity(dto: ExpenseRequestDTO, userId: Long): Expense
    fun mapToDTO(entity: Expense, total: Double): ExpenseResponseDTO
}
