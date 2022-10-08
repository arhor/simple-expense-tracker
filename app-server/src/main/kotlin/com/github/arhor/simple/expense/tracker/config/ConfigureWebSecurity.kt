package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ConfigureWebSecurity(
    private val applicationProps: ApplicationProps,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors { it.disable() }
            .csrf { it.disable() }
            .authorizeRequests {
                it.anyRequest().permitAll()
            }
            .logout {
                it.logoutUrl(URL_PATH_SIGN_OUT.withApiPrefix())
                it.logoutSuccessHandler(simpleUrlLogoutSuccessHandlerUsingReferer())
                it.logoutSuccessUrl(URL_PATH_ROOT)
            }
            .formLogin {
                it.loginPage(URL_PATH_SIGN_IN)
                it.loginProcessingUrl(URL_PATH_SIGN_IN.withApiPrefix())
            }
            .oauth2Login {
                it.loginPage(URL_PATH_SIGN_IN)
                it.authorizationEndpoint().baseUri(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI.withApiPrefix())
                it.successHandler(authenticationSuccessHandler)
            }
        return http.build()
    }

    private fun String.withApiPrefix() = applicationProps.apiUrlPath(this)

    private fun simpleUrlLogoutSuccessHandlerUsingReferer() = SimpleUrlLogoutSuccessHandler().apply {
        setUseReferer(true)
    }

    @Configuration(proxyBeanMethods = false)
    class Beans {
        @Bean
        fun passwordEncoder() = BCryptPasswordEncoder()
    }

    companion object {
        private const val URL_PATH_ROOT = "/"
        private const val URL_PATH_SIGN_IN = "/sign-in"
        private const val URL_PATH_SIGN_OUT = "/sign-out"
    }
}
