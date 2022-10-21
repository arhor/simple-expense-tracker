package com.github.arhor.simple.expense.tracker.service.mapping;

import com.github.arhor.simple.expense.tracker.data.model.Expense
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import java.math.BigDecimal

@Mapper(config = SharedMappingConfig::class)
interface ExpenseMapper {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    fun mapToEntity(dto: ExpenseRequestDTO, userId: Long): Expense

    fun mapToDTO(entity: Expense, total: Double): ExpenseResponseDTO
}
