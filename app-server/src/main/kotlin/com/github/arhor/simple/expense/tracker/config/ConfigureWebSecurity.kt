package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.web.util.UriComponentsBuilder

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class ConfigureWebSecurity {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        userService: UserService,
        appProps: ApplicationProps
    ): SecurityFilterChain {
        http.cors { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .logout {
                it.logoutUrl(appProps.apiUrlPath(URL_PATH_SIGN_OUT))
                it.logoutSuccessHandler(SimpleUrlLogoutSuccessHandler().apply { setUseReferer(true) })
                it.logoutSuccessUrl(URL_PATH_ROOT)
            }
            .formLogin {
                it.loginPage(URL_PATH_SIGN_IN)
                it.loginProcessingUrl(appProps.apiUrlPath(URL_PATH_SIGN_IN))
                it.successHandler(CustomSuccessHandler())
            }
            .oauth2Login {
                it.loginPage(URL_PATH_SIGN_IN)
                it.authorizationEndpoint().baseUri(appProps.authRequestBaseUri)
                it.successHandler(CustomSuccessHandler(doBeforeRedirect = userService::createNewUserIfNecessary))
            }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    companion object {
        private const val URL_PATH_ROOT = "/"
        private const val URL_PATH_SIGN_IN = "/sign-in"
        private const val URL_PATH_SIGN_OUT = "/sign-out"
        private const val PARAM_SUCCESS = "success"

        private object SuccessSignInRedirectStrategy : DefaultRedirectStrategy() {
            override fun calculateRedirectUrl(contextPath: String?, url: String?): String =
                super.calculateRedirectUrl(contextPath, url).let { path ->
                    if (path.endsWith(URL_PATH_SIGN_IN)) {
                        path.let(UriComponentsBuilder::fromUriString)
                            .queryParam(PARAM_SUCCESS)
                            .build()
                            .toString()
                    } else {
                        path
                    }
                }
        }

        private class CustomSuccessHandler(
            private val doBeforeRedirect: (Authentication) -> Unit = {}
        ) : SavedRequestAwareAuthenticationSuccessHandler() {

            init {
                setUseReferer(true)
                redirectStrategy = SuccessSignInRedirectStrategy
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
    }
}
