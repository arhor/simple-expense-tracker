package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.arhor.simple.expense.tracker.CurrentRequestContext;

@EnableAsync
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AsyncConfig implements AsyncConfigurer {

    private final ObjectProvider<CurrentRequestContext> currentRequestContextProvider;

    @Bean
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Bean
    @Override
    public Executor getAsyncExecutor() {
        return new DelegatingSecurityContextAsyncTaskExecutor(
            new ContextAwareThreadPoolTaskExecutor() {
                {
                    initialize();
                }
            }
        );
    }

    @SuppressWarnings("NullableProblems")
    private class ContextAwareThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

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

        @Override
        public void execute(final Runnable task, final long startTimeout) {
            super.execute(wrap(task), startTimeout);
        }

        private <T> Callable<T> wrap(final Callable<T> task) {
            var parentThreadRequestAttributes = RequestContextHolder.currentRequestAttributes();
            var currentRequestContext = currentRequestContextProvider.getObject();
            var parentThreadRequestId = currentRequestContext.getRequestId();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(parentThreadRequestAttributes);
                    currentRequestContext.setRequestId(parentThreadRequestId);
                    return task.call();
                } finally {
                    currentRequestContext.setRequestId(null);
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }

        private Runnable wrap(final Runnable task) {
            var parentThreadRequestAttributes = RequestContextHolder.currentRequestAttributes();
            var currentRequestContext = currentRequestContextProvider.getObject();
            var parentThreadRequestId = currentRequestContext.getRequestId();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(parentThreadRequestAttributes);
                    currentRequestContext.setRequestId(parentThreadRequestId);
                    task.run();
                } finally {
                    currentRequestContext.setRequestId(null);
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }
}
