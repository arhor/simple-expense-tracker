package com.github.arhor.simple.expense.tracker.service.mapping

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(config = SharedMappingConfig::class)
interface ExpenseItemMapper {

    @Mapping(target = "id", ignore = true)
    fun mapToEntity(dto: ExpenseItemDTO, expenseId: Long): ExpenseItem

    @InheritInverseConfiguration
    fun mapToDTO(entity: ExpenseItem): ExpenseItemDTO
}
