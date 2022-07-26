package com.github.arhor.simple.expense.tracker.config.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Mappings({
    @Mapping(target = "created", ignore = true),
    @Mapping(target = "updated", ignore = true),
})
public @interface IgnoreAuditProps {
}
