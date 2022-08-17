package com.github.arhor.simple.expense.tracker.web.error;

import com.github.arhor.simple.expense.tracker.web.CurrentRequestContext;
import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

import static com.github.arhor.simple.expense.tracker.util.TimeUtils.currentZonedDateTime;
import static com.github.arhor.simple.expense.tracker.web.error.ErrorCode.HANDLER_NOT_FOUND;
import static com.github.arhor.simple.expense.tracker.web.error.ErrorCode.HANDLER_NOT_FOUND_DEFAULT;
import static java.util.Collections.emptyList;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GlobalExceptionHandler {

    private final ApplicationProps applicationProps;
    private final MessageSource messages;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleDefault(final Exception exception, final Locale locale, final TimeZone timeZone) {
        log.error("Unhandled exception. Consider appropriate exception handler");
        return handleErrorCode(exception, ErrorCode.UNCATEGORIZED, locale, timeZone);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DataAccessException.class)
    public ErrorResponse handleDataAccessException(
        final DataAccessException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(exception, ErrorCode.DATA_COMMON, locale, timeZone);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(
        final EntityNotFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(exception, ErrorCode.NOT_FOUND, locale, timeZone, exception.getParams());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleUsernameNotFoundException(
        final UsernameNotFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(exception, ErrorCode.NOT_FOUND, locale, timeZone, "InternalUser", "provided username");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityDuplicateException.class)
    public ErrorResponse handleEntityDuplicateException(
        final EntityDuplicateException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(exception, ErrorCode.DUPLICATE, locale, timeZone, exception.getParams());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(
            exception,
            ErrorCode.METHOD_ARG_TYPE_MISMATCH,
            locale,
            timeZone,
            exception.getName(),
            exception.getValue(),
            exception.getRequiredType()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(
        final NoHandlerFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        val requestURL = exception.getRequestURL();

        if (requestURL.equals("/")) {
            return handleErrorCode(exception,
                HANDLER_NOT_FOUND_DEFAULT,
                locale,
                timeZone
            );
        } else if (requestURL.startsWith(applicationProps.apiUrlPath("/"))) {
            return handleErrorCode(exception,
                HANDLER_NOT_FOUND,
                locale,
                timeZone,
                exception.getHttpMethod(),
                requestURL
            );
        } else {
            return new ModelAndView("forward:/");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(
        final AccessDeniedException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(exception, ErrorCode.UNAUTHORIZED, locale, timeZone);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        val bindingResult = exception.getBindingResult();

        val fieldErrors = handleObjectErrors(
            bindingResult.getFieldErrors(),
            FieldError::getField,
            FieldError::getDefaultMessage
        );
        val globalErrors = handleObjectErrors(
            bindingResult.getGlobalErrors(),
            ObjectError::getObjectName,
            ObjectError::getDefaultMessage
        );

        return handleErrorCode(
            exception,
            ErrorCode.VALIDATION_FAILED,
            locale,
            timeZone,
            ListUtils.union(fieldErrors, globalErrors)
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException.class)
    public ErrorResponse handleFileNotFoundException(
        final FileNotFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(
            exception,
            ErrorCode.FILE_NOT_FOUND,
            locale,
            timeZone
        );
    }

    private ErrorResponse handleErrorCode(
        final Throwable exception,
        final ErrorCode errorCode,
        final Locale locale,
        final TimeZone timeZone,
        final Object... args
    ) {
        return handleErrorCode(exception, errorCode, locale, timeZone, emptyList(), args);
    }

    private ErrorResponse handleErrorCode(
        final Throwable exception,
        final ErrorCode errorCode,
        final Locale locale,
        final TimeZone timeZone,
        final List<String> details,
        final Object... args
    ) {
        log.error(exception.getMessage(), exception);

        return new ErrorResponse(
            CurrentRequestContext.getRequestId(),
            messages.getMessage(errorCode.getLabel(), args, locale),
            currentZonedDateTime(timeZone),
            errorCode,
            details
        );
    }

    private <T extends ObjectError> List<String> handleObjectErrors(
        final List<T> errors,
        final Function<T, String> nameProvider,
        final Function<T, String> messageProvider
    ) {
        val result = new ArrayList<String>(errors.size());

        for (val error : errors) {
            val name = nameProvider.apply(error);
            val message = messageProvider.apply(error);

            result.add(name + ": " + message);
        }
        return result;
    }
}
