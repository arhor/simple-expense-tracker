package com.github.arhor.simple.expense.tracker.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

class CustomSuccessHandler(
    private val doBeforeRedirect: (Authentication) -> Unit = {},
    private val redirectStrategy: RedirectStrategy = QueryParamRedirectStrategy.AuthSuccess,
) : SavedRequestAwareAuthenticationSuccessHandler() {

    init {
        setUseReferer(true)
        setRedirectStrategy(redirectStrategy)
    }

    override fun onAuthenticationSuccess(
        req: HttpServletRequest,
        res: HttpServletResponse,
        auth: Authentication
    ) {
        doBeforeRedirect(auth)
        super.onAuthenticationSuccess(req, res, auth)
    }
}
