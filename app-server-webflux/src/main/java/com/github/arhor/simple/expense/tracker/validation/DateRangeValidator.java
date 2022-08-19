package com.github.arhor.simple.expense.tracker.validation;

import lombok.val;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.arhor.simple.expense.tracker.DateRangeCriteria;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeCriteria> {

    @Override
    public boolean isValid(final DateRangeCriteria value, final ConstraintValidatorContext context) {
        if (value != null) {
            val startDate = value.startDate();
            val endDate = value.endDate();

            if ((startDate != null) && (endDate != null)) {
                return startDate.isBefore(endDate)
                    || startDate.isEqual(endDate);
            }
        }
        return true;
    }
}
