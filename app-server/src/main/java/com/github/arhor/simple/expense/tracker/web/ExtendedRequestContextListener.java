package com.github.arhor.simple.expense.tracker.web;

import javax.servlet.ServletRequestEvent;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

@Component
public class ExtendedRequestContextListener extends RequestContextListener {

    @Override
    public void requestInitialized(ServletRequestEvent requestEvent) {
        super.requestInitialized(requestEvent);
        RequestContextHolder.currentRequestAttributes().setAttribute(
            CurrentRequestContext.ATTR_CURRENT_REQUEST_CONTEXT,
            new CurrentRequestContext(),
            RequestAttributes.SCOPE_REQUEST
        );
    }
}
