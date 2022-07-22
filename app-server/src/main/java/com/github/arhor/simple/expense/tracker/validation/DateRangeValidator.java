package com.github.arhor.simple.expense.tracker.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.arhor.simple.expense.tracker.web.api.DateRangeCriteria;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeCriteria> {

    @Override
    public boolean isValid(final DateRangeCriteria value, final ConstraintValidatorContext context) {
        if (value == null || value.startDate() == null || value.endDate() == null) {
            return true;
        }
        return value.startDate().isAfter(value.endDate());
    }
}
