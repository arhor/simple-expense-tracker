package com.github.arhor.simple.expense.tracker.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Aspect
@Component
@ConditionalOnProperty(prefix = "application-props", name = ["log-method-execution"])
class MethodExecutionLoggingAspect {

    @Around("webLayer() || serviceLayer() || persistenceLayer()")
    fun logMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val log = joinPoint.componentLogger()

        if (log.isDebugEnabled) {
            val signature = joinPoint.signature
            val signatureName = "${signature.declaringType.simpleName}.${signature.name}()"

            log.debug("Method: {}, args: {}", signatureName, joinPoint.args.joinToString(", "))
            val result = joinPoint.proceed()
            log.debug("Method: {}, exit: {}", signatureName, result)

            return result
        }
        return joinPoint.proceed()
    }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.web..*(..))" +
            " && within(@org.springframework.web.bind.annotation.RestController *)"
    )
    private fun webLayer() { /* no-op */ }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.service..*(..))" +
            " && within(@org.springframework.stereotype.Service *)"
    )
    private fun serviceLayer() { /* no-op */ }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.data..*(..))" +
            " && within(@org.springframework.stereotype.Repository *)"
    )
    private fun persistenceLayer() { /* no-op */ }

    private fun JoinPoint.componentLogger(): Logger {
        return LoggerFactory.getLogger(signature.declaringTypeName)
    }
}

