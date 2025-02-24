package com.github.arhor.simple.expense.tracker.api

import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.RequestContextHolder
import java.lang.invoke.MethodHandles
import java.util.UUID

data class CurrentRequestContext(
    val requestId: UUID,
) {
    init {
        RequestContextHolder
            .currentRequestAttributes()
            .setAttribute(CURRENT_REQUEST_CONTEXT, this, SCOPE_REQUEST)
    }

    companion object {
        const val CURRENT_REQUEST_CONTEXT = "X-CURRENT-REQUEST-CONTEXT"

        private val log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

        fun get() = try {
            val attributes = RequestContextHolder.currentRequestAttributes()
            val context = attributes.getAttribute(CURRENT_REQUEST_CONTEXT, SCOPE_REQUEST) as CurrentRequestContext

            context
        } catch (e: IllegalStateException) {
            log.trace("Cannot get CurrentRequestContext instance from the request attributes", e)
            null
        }

        fun getRequestId(): String {
            return get()?.requestId?.toString()
                ?: "UNKNOWN"
        }
    }
}
