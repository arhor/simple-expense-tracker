package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.util.CollectionExt
import lombok.experimental.ExtensionMethod
import org.slf4j.MDC
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor
import org.springframework.web.context.request.RequestContextHolder
import java.util.concurrent.Executor

@EnableAsync
@ExtensionMethod(CollectionExt::class)
@Configuration(proxyBeanMethods = false)
class AsyncTasksConfig : AsyncConfigurer {

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return SimpleAsyncUncaughtExceptionHandler()
    }

    override fun getAsyncExecutor(): Executor {
        return DelegatingSecurityContextAsyncTaskExecutor(
            ThreadPoolTaskExecutor().apply {
                initialize()
                setTaskDecorator(::decorateUsingParentContext)
            }
        )
    }

    private fun decorateUsingParentContext(task: Runnable): Runnable {
        val attributes = RequestContextHolder.getRequestAttributes()
        val contextMap = MDC.getCopyOfContextMap()

        return if (attributes == null && contextMap == null) task else Runnable {
            try {
                RequestContextHolder.setRequestAttributes(attributes)
                MDC.setContextMap(contextMap ?: emptyMap())
                task.run()
            } finally {
                MDC.clear()
                RequestContextHolder.resetRequestAttributes()
            }
        }
    }
}
