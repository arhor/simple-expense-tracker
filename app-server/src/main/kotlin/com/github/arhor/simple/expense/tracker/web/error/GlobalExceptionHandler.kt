package com.github.arhor.simple.expense.tracker.web.error

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.util.currentZonedDateTime
import com.github.arhor.simple.expense.tracker.web.CurrentRequestContext
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.FileNotFoundException
import java.lang.invoke.MethodHandles
import java.util.Locale
import java.util.TimeZone

@RestControllerAdvice
class GlobalExceptionHandler(
    private val applicationProps: ApplicationProps,
    private val messages: MessageSource,
) {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleDefault(exception: Exception, locale: Locale, timeZone: TimeZone): ErrorResponse {
        log.error("Unhandled exception. Consider appropriate exception handler")
        return handleErrorCode(exception, ErrorCode.UNCATEGORIZED, locale, timeZone)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
        exception: DataAccessException,
        locale: Locale,
        timeZone: TimeZone
    ): ErrorResponse {
        return handleErrorCode(exception, ErrorCode.DATA_COMMON, locale, timeZone)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(
        exception: EntityNotFoundException,
        locale: Locale,
        timeZone: TimeZone,
    ): ErrorResponse {
        return handleErrorCode(exception, ErrorCode.NOT_FOUND, locale, timeZone, *exception.params)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        exception: UsernameNotFoundException,
        locale: Locale,
        timeZone: TimeZone
    ): ErrorResponse {
        return handleErrorCode(exception, ErrorCode.NOT_FOUND, locale, timeZone, "InternalUser", "provided username")
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityDuplicateException::class)
    fun handleEntityDuplicateException(
        exception: EntityDuplicateException,
        locale: Locale,
        timeZone: TimeZone
    ): ErrorResponse {
        return handleErrorCode(exception, ErrorCode.DUPLICATE, locale, timeZone, *exception.params)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(
        exception: MethodArgumentTypeMismatchException,
        locale: Locale,
        timeZone: TimeZone
    ): ErrorResponse {
        return handleErrorCode(
            exception,
            ErrorCode.METHOD_ARG_TYPE_MISMATCH,
            locale,
            timeZone,
            exception.name,
            exception.value,
            exception.requiredType
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        exception: NoHandlerFoundException,
        locale: Locale,
        timeZone: TimeZone,
    ): Any {
        val requestURL = exception.requestURL

        return when {
            requestURL == "/" -> {
                handleErrorCode(
                    exception,
                    ErrorCode.HANDLER_NOT_FOUND_DEFAULT,
                    locale,
                    timeZone
                )
            }

            requestURL.startsWith(applicationProps.apiUrlPath("/")) -> {
                handleErrorCode(
                    exception,
                    ErrorCode.HANDLER_NOT_FOUND,
                    locale,
                    timeZone,
                    exception.httpMethod,
                    requestURL
                )
            }

            else -> {
                ModelAndView("forward:/")
            }
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        exception: AccessDeniedException,
        locale: Locale,
        timeZone: TimeZone
    ): ErrorResponse {
        return handleErrorCode(exception, ErrorCode.UNAUTHORIZED, locale, timeZone)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
        locale: Locale,
        timeZone: TimeZone,
    ): ErrorResponse {
        val bindingResult = exception.bindingResult

        val fieldErrors = handleObjectErrors(
            bindingResult.fieldErrors,
            FieldError::getField,
            FieldError::getDefaultMessage
        )
        val globalErrors = handleObjectErrors(
            bindingResult.globalErrors,
            ObjectError::getObjectName,
            ObjectError::getDefaultMessage
        )

        return handleErrorCode(
            exception = exception,
            errorCode = ErrorCode.VALIDATION_FAILED,
            locale = locale,
            timeZone = timeZone,
            details = (fieldErrors + globalErrors),
            bindingResult.objectName,
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException::class)
    fun handleFileNotFoundException(
        exception: FileNotFoundException,
        locale: Locale,
        timeZone: TimeZone,
    ): ErrorResponse {
        return handleErrorCode(
            exception,
            ErrorCode.FILE_NOT_FOUND,
            locale,
            timeZone
        )
    }

    private fun handleErrorCode(
        exception: Throwable,
        errorCode: ErrorCode,
        locale: Locale,
        timeZone: TimeZone?,
        vararg args: Any?
    ): ErrorResponse = handleErrorCode(
        exception = exception,
        errorCode = errorCode,
        locale = locale,
        timeZone = timeZone,
        details = emptyList(),
        args = args
    )

    private fun handleErrorCode(
        exception: Throwable,
        errorCode: ErrorCode,
        locale: Locale,
        timeZone: TimeZone?,
        details: List<String> = emptyList(),
        vararg args: Any?
    ): ErrorResponse {
        log.error("An exception occurred:", exception)

        return ErrorResponse(
            requestId = CurrentRequestContext.getRequestId(),
            message = messages.getMessage(errorCode.label, args, locale),
            timestamp = currentZonedDateTime(timeZone),
            code = errorCode,
            details = details,
        )
    }

    private fun <T : ObjectError> handleObjectErrors(
        errors: List<T>,
        nameProvider: (T) -> String,
        messageProvider: (T) -> String?,
    ): List<String> {
        val result = ArrayList<String>(errors.size)

        for (error in errors) {
            val name = nameProvider(error)
            val message = messageProvider(error)

            result.add("$name: $message")
        }
        return result
    }

    companion object {
        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())
    }
}
