package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.IgnoreAuditProps;
import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.model.ExpenseRequestDTO;
import com.github.arhor.simple.expense.tracker.model.ExpenseResponseDTO;

@Mapper(config = MapStructConfig.class)
public interface ExpenseConverter {

    @IgnoreAuditProps
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Expense mapToEntity(ExpenseRequestDTO dto);

    @InheritInverseConfiguration
    @Mapping(target = "total", ignore = true)
    ExpenseResponseDTO mapToDTO(Expense entity);
}
