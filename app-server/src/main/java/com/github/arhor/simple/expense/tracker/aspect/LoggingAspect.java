package com.github.arhor.simple.expense.tracker.aspect;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.StringJoiner;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("dev")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggingAspect {

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

    @AfterThrowing(pointcut = "webLayer() || serviceLayer() || persistenceLayer()", throwing = "exception")
    public void logException(final JoinPoint joinPoint, final Throwable exception) {
        CurrentRequestContext.get().ifPresent(context -> {
            if (!context.isExceptionLogged(exception)) {
                componentLogger(joinPoint)
                    .error(
                        exception.getMessage(),
                        exception
                    );
                context.setExceptionBeenLogged(exception);
            }
        });
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

