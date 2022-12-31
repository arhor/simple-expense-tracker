package com.github.arhor.simple.expense.tracker.config.security

import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.web.util.UriComponentsBuilder

sealed class QueryParamRedirectStrategy(
    private val param: String,
    private val value: String,
) : DefaultRedirectStrategy() {

    override fun calculateRedirectUrl(contextPath: String?, url: String?): String =
        super.calculateRedirectUrl(contextPath, url).let {
            UriComponentsBuilder.fromUriString(it)
                .replaceQueryParam(param, value)
                .build()
                .toString()
        }

    object AuthSuccess : QueryParamRedirectStrategy(AUTH, SUCCESS)
    object AuthFailure : QueryParamRedirectStrategy(AUTH, FAILURE)

    companion object {
        private const val AUTH = "auth"
        private const val SUCCESS = "success"
        private const val FAILURE = "failure"
    }
}
