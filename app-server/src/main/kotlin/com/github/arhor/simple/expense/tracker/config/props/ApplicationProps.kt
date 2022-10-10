package com.github.arhor.simple.expense.tracker.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter

@ConstructorBinding
@ConfigurationProperties("application-props")
data class ApplicationProps(
    val apiPathPrefix: String?,
    val resources: Resources?,
    val conversionRates: ConversionRates?
) {

    val authorizationEndpointBaseUri: String
        get() = apiUrlPath(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)

    fun apiUrlPath(url: String): String {
        return (apiPathPrefix ?: "") + url
    }

    /**
     * @param patterns path patterns for the static resources
     * @param location locations to lookup for the static resources
     */
    data class Resources(
        val patterns: List<String>,
        val location: List<String>,
    )

    /**
     * @param pattern path-pattern for the data-files containing historical conversion-rates
     * @param preload number of previous years to preload starting from the last year
     */
    data class ConversionRates(
        val pattern: String?,
        val preload: Int?,
    )
}
