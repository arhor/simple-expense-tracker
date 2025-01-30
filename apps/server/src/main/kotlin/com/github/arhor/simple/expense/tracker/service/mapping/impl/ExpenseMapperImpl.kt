package com.github.arhor.simple.expense.tracker.service.mapping.impl

import com.github.arhor.simple.expense.tracker.data.model.Expense
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import com.github.arhor.simple.expense.tracker.service.mapping.ExpenseMapper
import org.springframework.stereotype.Component

@Component
class ExpenseMapperImpl : ExpenseMapper {

    override fun mapToEntity(dto: ExpenseRequestDTO, userId: Long) = Expense(
        userId = userId,
        name = dto.name,
        icon = dto.icon,
        color = dto.color,
    )

    override fun mapToDTO(entity: Expense, total: Double) = ExpenseResponseDTO(
        entity.id,
        entity.name,
        entity.icon,
        entity.color,
        total,
    )
}
