package com.github.arhor.simple.expense.tracker.service.mapping

import org.mapstruct.Mapping

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Mapping(target = "created", ignore = true)
@Mapping(target = "updated", ignore = true)
annotation class IgnoreAuditProps
