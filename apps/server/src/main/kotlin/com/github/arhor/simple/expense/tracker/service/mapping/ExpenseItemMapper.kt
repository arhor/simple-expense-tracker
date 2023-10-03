package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.model.ExpenseItemRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseItemResponseDTO
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = MapstructCommonConfig::class)
interface ExpenseItemMapper {

    @Mapping(target = "id", ignore = true)
    fun mapToEntity(dto: ExpenseItemRequestDTO, expenseId: Long): ExpenseItem

    @InheritInverseConfiguration
    fun mapToDTO(entity: ExpenseItem): ExpenseItemResponseDTO
}
