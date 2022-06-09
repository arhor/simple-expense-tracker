package com.github.arhor.simple.expense.tracker.config.mapping;

import java.util.Optional;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueMappingStrategy;

@MapperConfig(
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    uses = MapStructConfig.OptionalMapper.class
)
public class MapStructConfig {

    public static final class OptionalMapper {

        public static <T> Optional<T> wrap(T value) {
            return Optional.ofNullable(value);
        }

        public static <T> T unwrap(Optional<T> value) {
            return value.orElse(null);
        }
    }
}
