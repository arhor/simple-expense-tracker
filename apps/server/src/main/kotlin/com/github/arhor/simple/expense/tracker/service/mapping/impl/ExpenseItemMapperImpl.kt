package com.github.arhor.simple.expense.tracker.service.mapping.impl

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseItemMapper
import org.springframework.stereotype.Component

@Component
class ExpenseItemMapperImpl : ExpenseItemMapper {

    override fun mapToEntity(dto: ExpenseItemRequestDTO, expenseId: Long) = ExpenseItem(
        date = dto.date,
        amount = dto.amount.toBigDecimal(),
        currency = dto.currency.name,
        comment = dto.comment,
        expenseId = expenseId,
    )

    override fun mapToDTO(entity: ExpenseItem) = ExpenseItemResponseDTO(
        entity.id,
        entity.date,
        entity.amount.toDouble(),
        Currency.valueOf(entity.currency),
        entity.comment
    )
}
