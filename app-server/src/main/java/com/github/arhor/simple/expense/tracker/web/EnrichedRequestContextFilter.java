package com.github.arhor.simple.expense.tracker.web;

import lombok.val;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.aspect.CurrentRequestContext;

@Component
public class EnrichedRequestContextFilter extends OrderedRequestContextFilter {

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
                val context = new CurrentRequestContext();
                val requestId = servletReq.getHeader(REQUEST_ID);

                if (requestId != null) {
                    context.setRequestId(UUID.fromString(requestId));
                }
                context.setToCurrentRequestAttributes();

                servletRes.addHeader(REQUEST_ID, context.getRequestId().toString());
            }
            filterChain.doFilter(req, res);
        };
    }
}
