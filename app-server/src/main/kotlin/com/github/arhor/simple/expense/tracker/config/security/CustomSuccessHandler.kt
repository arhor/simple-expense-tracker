package com.github.arhor.simple.expense.tracker.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

class CustomSuccessHandler(
    private val doBeforeRedirect: (Authentication) -> Unit = {}
) : SavedRequestAwareAuthenticationSuccessHandler() {

    init {
        setUseReferer(true)
        redirectStrategy = QueryParamRedirectStrategy.AuthSuccess
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        doBeforeRedirect(authentication)
        super.onAuthenticationSuccess(request, response, authentication)
    }
}
