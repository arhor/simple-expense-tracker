package com.github.arhor.simple.expense.tracker.aspect;

import lombok.RequiredArgsConstructor;

import java.util.StringJoiner;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.github.arhor.simple.expense.tracker.CurrentRequestContext;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoggingAspect {

    private final ObjectProvider<CurrentRequestContext> currentRequestContextProvider;

    @Around("webLayer() || serviceLayer() || persistenceLayer()")
    public Object logMethodExecution(final ProceedingJoinPoint joinPoint) throws Throwable {
        var log = componentLogger(joinPoint);
        if (log.isDebugEnabled()) {
            var requestId = isRequestAvailable()
                ? currentRequestContextProvider.getObject().getRequestId().toString()
                : "UNKNOWN";
            var signature = joinPoint.getSignature();
            var signatureName = "%s.%s()".formatted(
                signature.getDeclaringType().getSimpleName(),
                signature.getName()
            );
            log.debug(
                "Request-ID: {}, Method: {}, Arguments: {}",
                requestId,
                signatureName,
                stringifyJoinPointArgs(joinPoint)
            );
            var result = joinPoint.proceed();
            log.debug(
                "Request-ID: {}, Method: {}, Result: {}",
                requestId,
                signatureName,
                result
            );
            return result;
        }
        return joinPoint.proceed();
    }

    @AfterThrowing(pointcut = "webLayer() || serviceLayer() || persistenceLayer()", throwing = "exception")
    public void logException(final JoinPoint joinPoint, final Throwable exception) {
        if (isRequestAvailable()) {
            currentRequestContextProvider.ifAvailable(currentRequestContext -> {
                if (!currentRequestContext.isExceptionLogged(exception)) {
                    componentLogger(joinPoint).error(
                        "Request-ID: {}",
                        currentRequestContext.getRequestId(),
                        exception
                    );
                    currentRequestContext.setExceptionBeenLogged(exception);
                }
            });
        }
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

    private boolean isRequestAvailable() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    private String stringifyJoinPointArgs(final JoinPoint joinPoint) {
        final var result = new StringJoiner(", ");
        for (final var arg : joinPoint.getArgs()) {
            result.add(String.valueOf(arg));
        }
        return result.toString();
    }
}

