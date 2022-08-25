package com.github.arhor.simple.expense.tracker.validation

import com.github.arhor.simple.expense.tracker.service.DateRangeCriteria
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class DateRangeValidator : ConstraintValidator<ValidDateRange, DateRangeCriteria> {

    override fun isValid(value: DateRangeCriteria?, context: ConstraintValidatorContext): Boolean {
        value?.let { (startDate, endDate) ->
            if ((startDate != null) && (endDate != null)) {
                return startDate.isBefore(endDate)
                    || startDate.isEqual(endDate)
            }
        }
        return true
    }
}