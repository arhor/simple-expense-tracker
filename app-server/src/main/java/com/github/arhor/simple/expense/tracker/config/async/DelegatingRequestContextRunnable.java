package com.github.arhor.simple.expense.tracker.config.async;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @param delegate   {@link Runnable} to be wrapped
 * @param attributes {@link RequestAttributes} from the thread initialized this task
 */
record DelegatingRequestContextRunnable(
    Runnable delegate,
    RequestAttributes attributes
) implements Runnable {

    @Override
    public void run() {
        try {
            RequestContextHolder.setRequestAttributes(attributes);
            delegate.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }
}
