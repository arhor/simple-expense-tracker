package com.github.arhor.simple.expense.tracker.service.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;

@MapperConfig(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    uses = OptionalMapper.class
)
public final class SharedMappingConfig {}
