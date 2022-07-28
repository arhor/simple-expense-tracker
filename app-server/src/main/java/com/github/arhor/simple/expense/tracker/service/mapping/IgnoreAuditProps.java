package com.github.arhor.simple.expense.tracker.service.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Mapping(target = "created", ignore = true)
@Mapping(target = "updated", ignore = true)
public @interface IgnoreAuditProps {
}
