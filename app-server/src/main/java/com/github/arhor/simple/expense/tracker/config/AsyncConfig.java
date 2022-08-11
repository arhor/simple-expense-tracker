package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import com.github.arhor.simple.expense.tracker.config.async.ContextAwareThreadPoolTaskExecutor;

@EnableAsync
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AsyncConfig implements AsyncConfigurer {

    public static final String ASYNC_EXECUTOR_BEAN = "ContextAwareThreadPoolTaskExecutor";

    @Bean
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Bean(ASYNC_EXECUTOR_BEAN)
    @Override
    public Executor getAsyncExecutor() {
        val executor = new ContextAwareThreadPoolTaskExecutor();
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
