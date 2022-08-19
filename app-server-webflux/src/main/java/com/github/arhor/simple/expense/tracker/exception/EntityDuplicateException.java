package com.github.arhor.simple.expense.tracker.exception;

public final class EntityDuplicateException extends PropertyConditionException {

    public EntityDuplicateException(final String name, final String condition) {
        super(name, condition);
    }
}
