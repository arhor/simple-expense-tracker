package com.github.arhor.simple.expense.tracker.config;

import lombok.experimental.ExtensionMethod;
import lombok.val;

import java.util.concurrent.Executor;

import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.arhor.simple.expense.tracker.util.CollectionExt;

@EnableAsync
@ExtensionMethod(CollectionExt.class)
@Configuration(proxyBeanMethods = false)
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Override
    public Executor getAsyncExecutor() {
        val executor = new ThreadPoolTaskExecutor();

        executor.initialize();
        executor.setTaskDecorator(this::decorate);

        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

    private Runnable decorate(final Runnable delegate) {
        val attributes = RequestContextHolder.getRequestAttributes();
        val contextMap = MDC.getCopyOfContextMap();

        return ((attributes == null) && (contextMap == null)) ? delegate : () -> {
            try {
                RequestContextHolder.setRequestAttributes(attributes);
                MDC.setContextMap(contextMap.emptyIfNull());
                delegate.run();
            } finally {
                MDC.clear();
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
