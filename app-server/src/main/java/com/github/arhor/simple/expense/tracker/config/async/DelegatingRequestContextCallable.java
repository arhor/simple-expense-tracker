package com.github.arhor.simple.expense.tracker.config.async;

import java.util.concurrent.Callable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @param delegate   {@link Callable} to be wrapped
 * @param attributes {@link RequestAttributes} from the thread initialized this task
 */
record DelegatingRequestContextCallable<T>(
    Callable<T> delegate,
    RequestAttributes attributes
) implements Callable<T> {

    @Override
    public T call() throws Exception {
        try {
            RequestContextHolder.setRequestAttributes(attributes);
            return delegate.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
