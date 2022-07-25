package com.github.arhor.simple.expense.tracker.aspect;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import static com.github.arhor.simple.expense.tracker.aspect.CurrentRequestContext.CURRENT_REQUEST_CONTEXT;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Component
public class ExtendedRequestContextListener extends OrderedRequestContextFilter {

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
            if ((req instanceof HttpServletRequest request) && (res instanceof HttpServletResponse response)) {
                var context = new CurrentRequestContext();
                var requestId = request.getHeader(REQUEST_ID);

                if (requestId != null) {
                    context.setRequestId(UUID.fromString(requestId));
                }

                RequestContextHolder
                    .currentRequestAttributes()
                    .setAttribute(CURRENT_REQUEST_CONTEXT, context, SCOPE_REQUEST);

                response.addHeader(REQUEST_ID, context.getRequestId().toString());
            }
            filterChain.doFilter(req, res);
        };
    }
}
