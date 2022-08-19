package com.github.arhor.simple.expense.tracker.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PropertyConditionException extends RuntimeException {

    private final String name;
    private final String condition;

    public Object[] getParams() {
        return new Object[]{name, condition};
    }
}
