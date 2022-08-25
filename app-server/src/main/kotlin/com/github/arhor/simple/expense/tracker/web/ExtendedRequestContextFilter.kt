package com.github.arhor.simple.expense.tracker.web

import org.slf4j.MDC
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ExtendedRequestContextFilter : OrderedRequestContextFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, next: FilterChain) {
        super.doFilterInternal(req, res, next.withContextExtension())
    }

    private fun FilterChain.withContextExtension() = FilterChain { req, res ->
        if ((req is HttpServletRequest) && (res is HttpServletResponse)) {
            val requestId = Optional.ofNullable(req.getHeader(REQUEST_ID))
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID)
                .also { CurrentRequestContext(it) }
                .toString()

            MDC.put("request-id", requestId)
            res.addHeader(REQUEST_ID, requestId)
        }
        try {
            doFilter(req, res)
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private const val REQUEST_ID = "X-REQUEST-ID"
    }
}
