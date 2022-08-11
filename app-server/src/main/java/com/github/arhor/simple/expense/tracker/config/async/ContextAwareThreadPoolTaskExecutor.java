package com.github.arhor.simple.expense.tracker.config.async;

import lombok.val;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.request.RequestContextHolder;

@SuppressWarnings("NullableProblems")
public class ContextAwareThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return super.submit(wrap(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(final Callable<T> task) {
        return super.submitListenable(wrap(task));
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return super.submit(wrap(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(final Runnable task) {
        return super.submitListenable(wrap(task));
    }

    @Override
    public void execute(final Runnable task) {
        super.execute(wrap(task));
    }

    private <T> Callable<T> wrap(final Callable<T> task) {
        val attributes = RequestContextHolder.getRequestAttributes();

        return (attributes != null)
            ? new DelegatingRequestContextCallable<>(task, attributes)
            : task;
    }

    private Runnable wrap(final Runnable task) {
        val attributes = RequestContextHolder.getRequestAttributes();

        return (attributes != null)
            ? new DelegatingRequestContextRunnable(task, attributes)
            : task;
    }
}
