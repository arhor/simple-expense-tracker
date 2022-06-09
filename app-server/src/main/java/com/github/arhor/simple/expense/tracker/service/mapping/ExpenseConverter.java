package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.github.arhor.simple.expense.tracker.config.mapping.IgnoreAuditProps;
import com.github.arhor.simple.expense.tracker.config.mapping.MapStructConfig;
import com.github.arhor.simple.expense.tracker.data.model.Expense;
import com.github.arhor.simple.expense.tracker.model.ExpenseDTO;

@Mapper(config = MapStructConfig.class)
public interface ExpenseConverter {

    @IgnoreAuditProps
    @Mapping(target = "userId", ignore = true)
    Expense mapDtoToEntity(ExpenseDTO item);

    @InheritInverseConfiguration
    @Mapping(target = "total", ignore = true)
    ExpenseDTO mapEntityToDto(Expense item);
}
