package com.github.arhor.simple.expense.tracker;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
public class CurrentRequestContextImpl implements CurrentRequestContext {

    @Getter
    @Setter
    private UUID requestId = UUID.randomUUID();
    private Set<Throwable> loggedExceptions;

    @Override
    public boolean isExceptionLogged(final Throwable throwable) {
        return (loggedExceptions != null) && loggedExceptions.contains(throwable);
    }

    @Override
    public void setExceptionBeenLogged(final Throwable throwable) {
        if (loggedExceptions == null) {
            loggedExceptions = new HashSet<>();
        }
        loggedExceptions.add(throwable);
    }
}
