package com.github.arhor.simple.expense.tracker.service.mapping;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants
import org.mapstruct.NullValueMappingStrategy;

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    uses = [OptionalMapper::class],
)
class MapstructCommonConfig
