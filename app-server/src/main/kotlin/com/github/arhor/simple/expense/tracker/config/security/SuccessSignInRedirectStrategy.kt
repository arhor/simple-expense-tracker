package com.github.arhor.simple.expense.tracker.config.security

import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.web.util.UriComponentsBuilder

object SuccessSignInRedirectStrategy : DefaultRedirectStrategy() {

    private const val PARAM_SUCCESS = "success"

    override fun calculateRedirectUrl(contextPath: String?, url: String?): String =
        super.calculateRedirectUrl(contextPath, url).let { path ->
            if (path.endsWith("/sign-in")) {
                path.let(UriComponentsBuilder::fromUriString)
                    .queryParam(PARAM_SUCCESS)
                    .build()
                    .toString()
            } else {
                path
            }
        }
}
