package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.ExpenseItem;
import com.github.arhor.simple.expense.tracker.model.ExpenseItemDTO;

@Mapper(config = MapStructConfig.class)
public interface ExpenseItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expenseId", ignore = true)
    ExpenseItem mapToEntity(ExpenseItemDTO dto);

    @InheritInverseConfiguration
    ExpenseItemDTO mapToDTO(ExpenseItem entity);
}
