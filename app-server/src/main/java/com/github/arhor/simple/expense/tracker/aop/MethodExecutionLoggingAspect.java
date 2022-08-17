package com.github.arhor.simple.expense.tracker.aop;

import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Aspect
@Component
@ConditionalOnProperty(prefix = "application-props", name = "log-method-execution")
public class MethodExecutionLoggingAspect {

    @Around("webLayer() || serviceLayer() || persistenceLayer()")
    public Object logMethodExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
        val log = componentLogger(joinPoint);

        if (log.isDebugEnabled()) {
            val signature = joinPoint.getSignature();
            val signatureName = "%s.%s()".formatted(
                signature.getDeclaringType().getSimpleName(),
                signature.getName()
            );
            log.debug("Method: {}, args: {}", signatureName, stringifyJoinPointArgs(joinPoint));
            val result = joinPoint.proceed();
            log.debug("Method: {}, exit: {}", signatureName, result);
            return result;
        }
        return joinPoint.proceed();
    }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.web..*(..))" +
            " && within(@org.springframework.web.bind.annotation.RestController *)"
    )
    private void webLayer() { /* no-op */ }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.service..*(..))" +
            " && within(@org.springframework.stereotype.Service *)"
    )
    private void serviceLayer() { /* no-op */ }

    @Pointcut(
        value = "execution(* com.github.arhor.simple.expense.tracker.data..*(..))" +
            " && within(@org.springframework.stereotype.Repository *)"
    )
    private void persistenceLayer() { /* no-op */ }

    private Logger componentLogger(final JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    private String stringifyJoinPointArgs(final JoinPoint joinPoint) {
        val result = new StringJoiner(", ");
        for (val arg : joinPoint.getArgs()) {
            result.add(String.valueOf(arg));
        }
        return result.toString();
    }
}

