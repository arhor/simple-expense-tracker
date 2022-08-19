package com.github.arhor.simple.expense.tracker.service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;

@Mapper(config = SharedMappingConfig.class)
public interface ExpenseItemMapper {

    @Mapping(target = "id", ignore = true)
    ExpenseItem mapToEntity(ExpenseItemDTO dto, Long expenseId);

    @InheritInverseConfiguration
    ExpenseItemDTO mapToDTO(ExpenseItem entity);
}
