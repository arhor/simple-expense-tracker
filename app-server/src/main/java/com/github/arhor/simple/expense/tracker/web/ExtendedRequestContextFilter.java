package com.github.arhor.simple.expense.tracker.web;

import com.github.arhor.simple.expense.tracker.util.JavaLangExt;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@ExtensionMethod(JavaLangExt.class)
@SuppressWarnings("NullableProblems")
public class ExtendedRequestContextFilter extends OrderedRequestContextFilter {

    private static final String REQUEST_ID = "X-REQUEST-ID";

    @Override
    protected void doFilterInternal(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain filterChain
    ) throws ServletException, IOException {
        super.doFilterInternal(request, response, wrap(filterChain));
    }

    private FilterChain wrap(final FilterChain filterChain) {
        return (req, res) -> {
            if ((req instanceof HttpServletRequest servletReq) && (res instanceof HttpServletResponse servletRes)) {
                val requestId = Optional.ofNullable(servletReq.getHeader(REQUEST_ID))
                    .map(UUID::fromString)
                    .orElseGet(UUID::randomUUID)
                    .also(CurrentRequestContext::new)
                    .toString();

                MDC.put("request-id", requestId);
                servletRes.addHeader(REQUEST_ID, requestId);
            }
            try {
                filterChain.doFilter(req, res);
            } finally {
                MDC.clear();
            }
        };
    }
}
