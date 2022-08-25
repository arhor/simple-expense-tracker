package com.github.arhor.simple.expense.tracker.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.reflect.KClass

@Target(VALUE_PARAMETER)
@Retention(RUNTIME)
@MustBeDocumented
@Constraint(
    validatedBy = [
        /* @see resources/META-INF/services/javax.validation.ConstraintValidator */
    ]
)
annotation class ValidDateRange(
    val message: String = "{com.acme.constraint.OrderNumber.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<in Payload>> = [],
)
