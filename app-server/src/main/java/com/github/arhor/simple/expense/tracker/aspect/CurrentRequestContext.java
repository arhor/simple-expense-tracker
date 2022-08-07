package com.github.arhor.simple.expense.tracker.aspect;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.context.request.RequestContextHolder;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class CurrentRequestContext {

    public static final String CURRENT_REQUEST_CONTEXT = "X-CURRENT-REQUEST-CONTEXT";

    @Getter
    @Setter
    private UUID requestId = UUID.randomUUID();
    private Set<Throwable> loggedExceptions;

    public static Optional<CurrentRequestContext> get() {
        try {
            val attributes = RequestContextHolder.currentRequestAttributes();
            val context = (CurrentRequestContext) attributes.getAttribute(CURRENT_REQUEST_CONTEXT, SCOPE_REQUEST);
            return Optional.ofNullable(context);
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    public static String requestId() {
        return get()
            .map(CurrentRequestContext::getRequestId)
            .map(Objects::toString)
            .orElse("UNKNOWN");
    }

    public boolean isExceptionLogged(final Throwable throwable) {
        return (loggedExceptions != null) && loggedExceptions.contains(throwable);
    }

    public void setExceptionBeenLogged(final Throwable throwable) {
        if (loggedExceptions == null) {
            loggedExceptions = new HashSet<>();
        }
        loggedExceptions.add(throwable);
    }
}
