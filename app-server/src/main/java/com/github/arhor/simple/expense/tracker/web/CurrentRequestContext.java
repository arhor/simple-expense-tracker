package com.github.arhor.simple.expense.tracker.web;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
public record CurrentRequestContext(
    UUID requestId
) {

    public static final String CURRENT_REQUEST_CONTEXT = "X-CURRENT-REQUEST-CONTEXT";

    public CurrentRequestContext {
        RequestContextHolder
            .currentRequestAttributes()
            .setAttribute(CURRENT_REQUEST_CONTEXT, this, SCOPE_REQUEST);
    }

    public static Optional<CurrentRequestContext> get() {
        try {
            val attributes = RequestContextHolder.currentRequestAttributes();
            val context = (CurrentRequestContext) attributes.getAttribute(CURRENT_REQUEST_CONTEXT, SCOPE_REQUEST);

            return Optional.ofNullable(context);
        } catch (IllegalStateException e) {
            log.trace("Cannot get CurrentRequestContext instance from the request attributes", e);
            return Optional.empty();
        }
    }

    public static String getRequestId() {
        return get()
            .map(CurrentRequestContext::requestId)
            .map(Objects::toString)
            .orElse("UNKNOWN");
    }
}
