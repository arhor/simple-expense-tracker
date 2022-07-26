package com.github.arhor.simple.expense.tracker.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeCriteria> {

    @Override
    public boolean isValid(final DateRangeCriteria value, final ConstraintValidatorContext context) {
        if (value != null) {
            var startDate = value.startDate();
            var endDate = value.endDate();

            if ((startDate != null) && (endDate != null)) {
                return startDate.isBefore(endDate)
                    || startDate.isEqual(endDate);
            }
        }
        return true;
    }
}
