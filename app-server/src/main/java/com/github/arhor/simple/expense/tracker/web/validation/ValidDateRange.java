package com.github.arhor.simple.expense.tracker.web.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
@Constraint(
    validatedBy = {
        /* @see resources/META-INF/services/javax.validation.ConstraintValidator */
    }
)
public @interface ValidDateRange {

    String message() default "{com.acme.constraint.OrderNumber.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
