package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO

interface ExpenseItemMapper {

    fun mapToEntity(dto: ExpenseItemRequestDTO, expenseId: Long): ExpenseItem

    fun mapToDTO(entity: ExpenseItem): ExpenseItemResponseDTO
}
