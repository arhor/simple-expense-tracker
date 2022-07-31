package com.github.arhor.simple.expense.tracker.service.mapping;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;

@Mapper(config = SharedMappingConfig.class)
public interface ExpenseMapper {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    Expense mapToEntity(ExpenseRequestDTO dto, Long userId);

    ExpenseResponseDTO mapToDTO(Expense entity, BigDecimal total);
}
