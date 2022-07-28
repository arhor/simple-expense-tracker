package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.model.ExpenseDetailsResponseDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;

@Mapper(config = SharedMappingConfig.class)
public interface ExpenseMapper {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    Expense mapToEntity(ExpenseRequestDTO dto, Long userId);

    @InheritInverseConfiguration
    @Mapping(target = "total", ignore = true)
    ExpenseResponseDTO mapToDTO(Expense entity);

    @Mapping(target = "total", ignore = true)
    @Mapping(target = "items", ignore = true)
    ExpenseDetailsResponseDTO mapToDetailsDTO(Expense entity);
}
