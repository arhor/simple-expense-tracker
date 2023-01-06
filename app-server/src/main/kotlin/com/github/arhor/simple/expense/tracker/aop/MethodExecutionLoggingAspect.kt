package com.github.arhor.simple.expense.tracker.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Aspect
@Component
@ConditionalOnProperty(prefix = "application-props", name = ["log-method-execution"])
class MethodExecutionLoggingAspect {

    @OptIn(ExperimentalTime::class)
    @Around("webLayer() || serviceLayer() || dataLayer()")
    fun logMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val logger = joinPoint.componentLogger()
        val signature = joinPoint.signature as MethodSignature
        val methodName = signature.name
        val methodArgs = joinPoint.args.contentToString()

        logger.info("Method: $methodName() >>> args: $methodArgs")
        val (result, duration) = measureTimedValue { joinPoint.proceed() }
        logger.info("Method: $methodName() <<< exit: ${signature.format(result)}, time: $duration")

        return result
    }

    @Pointcut(
        value = "execution(* $ROOT.web..*(..)) && within(@org.springframework.web.bind.annotation.RestController *)"
    )
    private fun webLayer() {
        /* no-op */
    }

    @Pointcut(
        value = "execution(* $ROOT.service..*(..)) && within(@org.springframework.stereotype.Service *)"
    )
    private fun serviceLayer() {
        /* no-op */
    }

    @Pointcut(
        value = "execution(* $ROOT.data..*(..)) && within(@org.springframework.stereotype.Repository *)"
    )
    private fun dataLayer() {
        /* no-op */
    }

    companion object {
        private const val ROOT = "com.github.arhor.simple.expense.tracker"
        private const val VOID = "void"

        private fun JoinPoint.componentLogger(): Logger {
            return LoggerFactory.getLogger(signature.declaringTypeName)
        }

        private fun MethodSignature.format(result: Any?): Any? {
            return if (returnType.name == VOID) {
                VOID
            } else {
                result
            }
        }
    }
}

