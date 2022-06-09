package com.github.arhor.simple.expense.tracker.web.error;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Function;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import com.github.arhor.simple.expense.tracker.CurrentRequestContext;
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.service.TimeService;

import static com.github.arhor.simple.expense.tracker.config.WebServerConfig.apiUrlPath;
import static com.github.arhor.simple.expense.tracker.web.error.ErrorCode.HANDLER_NOT_FOUND;
import static com.github.arhor.simple.expense.tracker.web.error.ErrorCode.HANDLER_NOT_FOUND_DEFAULT;
import static java.util.Collections.emptyList;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GlobalExceptionHandler {

    private final MessageSource messages;
    private final TimeService timeService;
    private final ObjectProvider<CurrentRequestContext> currentRequestContextProvider;

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleDefault(final Exception exception, final Locale locale, final TimeZone timeZone) {
        log.error("Unhandled exception. Consider appropriate exception handler", exception);
        return handleErrorCode(ErrorCode.UNCATEGORIZED, locale, timeZone);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(
        final EntityNotFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(ErrorCode.NOT_FOUND, locale, timeZone, exception.getParams());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse handleUsernameNotFoundException(
        final UsernameNotFoundException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(ErrorCode.NOT_FOUND, locale, timeZone, "InternalUser", "provided username");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException.class)
    public ErrorResponse handleEntityDuplicateException(
        final EntityDuplicateException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        return handleErrorCode(ErrorCode.DUPLICATE, locale, timeZone, exception.getParams());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException exception,
        Locale locale,
        TimeZone timeZone
    ) {
        return handleErrorCode(
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
        var requestURL = exception.getRequestURL();

        final Object result;
        if (requestURL.equals("/")) {
            result = handleErrorCode(HANDLER_NOT_FOUND_DEFAULT, locale, timeZone);
        } else if (requestURL.startsWith(apiUrlPath("/"))) {
            result = handleErrorCode(HANDLER_NOT_FOUND, locale, timeZone, exception.getHttpMethod(), requestURL);
        } else {
            result = new ModelAndView("forward:/");
        }
        return result;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(Locale locale, TimeZone timeZone) {
        return handleErrorCode(ErrorCode.UNAUTHORIZED, locale, timeZone);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception,
        final Locale locale,
        final TimeZone timeZone
    ) {
        var bindingResult = exception.getBindingResult();

        var fieldErrors = handleObjectErrors(
            bindingResult.getFieldErrors(),
            FieldError::getField,
            FieldError::getDefaultMessage
        );
        var globalErrors = handleObjectErrors(
            bindingResult.getGlobalErrors(),
            ObjectError::getObjectName,
            ObjectError::getDefaultMessage
        );

        return handleErrorCode(
            ErrorCode.VALIDATION_FAILED,
            locale,
            timeZone,
            ListUtils.union(fieldErrors, globalErrors)
        );
    }

    private ErrorResponse handleErrorCode(
        final ErrorCode error,
        final Locale locale,
        final TimeZone timeZone,
        final Object... args
    ) {
        return handleErrorCode(error, locale, timeZone, emptyList(), args);
    }

    private ErrorResponse handleErrorCode(
        final ErrorCode error,
        final Locale locale,
        final TimeZone timeZone,
        final List<String> details,
        final Object... args
    ) {
        var currentRequestContext = currentRequestContextProvider.getIfAvailable();
        var requestId = (currentRequestContext != null) ? currentRequestContext.getRequestId().toString() : "UNKNOWN";

        return new ErrorResponse(
            requestId,
            messages.getMessage(error.getLabel(), args, locale),
            timeService.now(timeZone),
            error,
            details
        );
    }

    private <T extends ObjectError> List<String> handleObjectErrors(
        final List<T> errors,
        final Function<T, String> nameProvider,
        final Function<T, String> messageProvider
    ) {
        var result = new ArrayList<String>(errors.size());

        for (var error : errors) {
            var name = nameProvider.apply(error);
            var message = messageProvider.apply(error);

            result.add(name + ": " + message);
        }
        return result;
    }

    //    private fun handleResponseStatusException(ex: ResponseStatusException): Pair<HttpStatus, MessageResponse> {
    //        logger.error("Unhandled error. Please, create proper exception handler for it.", ex)
    //        return ex.status to messageResponse {
    //            error {
    //                text = "Internal Server Error. Please, contact system administrator."
    //            }
    //        }
    //    }
    //
    //    private fun handleFileNotFoundException(ex: FileNotFoundException): Pair<HttpStatus, MessageResponse> {
    //        return HttpStatus.NOT_FOUND to messageResponse {
    //            error {
    //                code = ErrorCode.FILE_NOT_FOUND
    //                text = "File Not Found"
    //                details = listOf(ex.message)
    //            }
    //        }
    //    }
    //
    //    private fun handleMethodArgumentTypeMismatchException(
    //        ex: MethodArgumentTypeMismatchException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        return HttpStatus.BAD_REQUEST to messageResponse {
    //            error {
    //                code = ErrorCode.UNCATEGORIZED
    //                text = lang.localize("error.wrong.argument")
    //                details = listOf(
    //                    lang.localize("error.wrong.argument.details", ex.name, ex.value)
    //                )
    //            }
    //        }
    //    }
    //
    //    private fun handleDataAccessException(ex: DataAccessException, lang: Locale): Pair<HttpStatus, MessageResponse> {
    //        return HttpStatus.NOT_FOUND to messageResponse {
    //            error {
    //                code = ErrorCode.DATA_ACCESS_ERROR
    //                text = lang.localize("error.data.uncategorized")
    //                details = listOf(ex.message)
    //            }
    //        }
    //    }
    //
    //    private fun handleEntityNotFoundException(
    //        ex: EntityNotFoundException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        val (entityType, propName, propValue) = ex
    //
    //        return HttpStatus.NOT_FOUND to messageResponse {
    //            error {
    //                code = ErrorCode.NOT_FOUND
    //                text = lang.localize("error.entity.notfound")
    //                details = listOf(
    //                    lang.localize("error.entity.notfound.details", entityType, propName, propValue)
    //                )
    //            }
    //        }
    //    }
    //
    //    private fun handleInvalidFormatException(
    //        ex: InvalidFormatException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        val parser = ex.processor as JsonParser
    //
    //        return HttpStatus.BAD_REQUEST to messageResponse {
    //            error {
    //                code = ErrorCode.VALIDATION_FAILED
    //                text = lang.localize("error.json.format.invalid")
    //                details = listOf(
    //                    lang.localize(
    //                        "error.json.format.invalid.details",
    //                        ex.value,
    //                        parser.currentName,
    //                        ex.targetType.simpleName
    //                    )
    //                )
    //            }
    //        }
    //    }
    //
    //    private fun handleJsonProcessingException(
    //        ex: JsonProcessingException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        val value = when (val processor = ex.processor) {
    //            is JsonParser -> processor.currentName ?: processor.text
    //            else -> "[UNKNOWN JSON PROCESSOR]"
    //        }
    //
    //        return HttpStatus.BAD_REQUEST to messageResponse {
    //            error {
    //                code = ErrorCode.UNCATEGORIZED
    //                text = lang.localize("error.json.parse")
    //                details = listOf(
    //                    lang.localize(
    //                        "error.json.parse.details",
    //                        value,
    //                        ex.location.lineNr,
    //                        ex.location.columnNr
    //                    )
    //                )
    //            }
    //        }
    //    }
    //
    //    private fun handleMethodArgumentNotValidException(
    //        ex: MethodArgumentNotValidException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        return HttpStatus.BAD_REQUEST to messageResponse {
    //            error {
    //                code = ErrorCode.VALIDATION_FAILED
    //                text = lang.localize("Object validation failed")
    //                details = errorMessagesGroupedByField(ex, lang)
    //            }
    //        }
    //    }
    //
    //    private fun handleAuthenticationException(
    //        ex: AuthenticationException,
    //        lang: Locale
    //    ): Pair<HttpStatus, MessageResponse> {
    //        logger.error { "Authentication failed: ${ex.message}" }
    //        return HttpStatus.UNAUTHORIZED to messageResponse {
    //            error {
    //                code = ErrorCode.SECURITY_VIOLATION
    //                text = lang.localize("error.credentials.bad")
    //            }
    //        }
    //    }
    //
    //    private fun errorMessagesGroupedByField(
    //        ex: MethodArgumentNotValidException,
    //        lang: Locale
    //    ): List<FieldErrorDetails> {
    //
    //        return ex.allErrors
    //            .map { it as FieldError }
    //            .groupBy({ it.field }, { it.defaultMessage })
    //            .map { (field, messages) ->
    //                FieldErrorDetails(
    //                    field = lang.localize("error.validation.failed", field),
    //                    messages = messages.filterNotNull()
    //                )
    //            }
    //    }
}
