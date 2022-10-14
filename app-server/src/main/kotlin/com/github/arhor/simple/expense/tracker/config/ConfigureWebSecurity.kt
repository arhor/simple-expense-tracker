package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ConfigureWebSecurity {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        userService: UserService,
        appProps: ApplicationProps
    ): SecurityFilterChain {
        http.cors { it.disable() }
            .csrf { it.disable() }
            .authorizeRequests {
                it.anyRequest().permitAll()
            }
            .logout {
                it.logoutUrl(appProps.apiUrlPath(URL_PATH_SIGN_OUT))
                it.logoutSuccessHandler(SimpleUrlLogoutSuccessHandler().also(::configure))
                it.logoutSuccessUrl(URL_PATH_ROOT)
            }
            .formLogin {
                it.loginPage(URL_PATH_SIGN_IN)
                it.loginProcessingUrl(appProps.apiUrlPath(URL_PATH_SIGN_IN))
            }
            .oauth2Login {
                it.loginPage(URL_PATH_SIGN_IN)
                it.authorizationEndpoint().baseUri(appProps.authRequestBaseUri)
                it.successHandler(CustomSuccessHandler(userService::createNewUserIfNecessary).also(::configure))
            }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    private fun configure(handler: AbstractAuthenticationTargetUrlRequestHandler) {
        handler.setUseReferer(true)
    }

    class CustomSuccessHandler(
        private val doBeforeRedirect: (Authentication) -> Unit
    ) : SavedRequestAwareAuthenticationSuccessHandler() {

        init {
            redirectStrategy = SuccessSignInRedirectStrategy
        }

        override fun onAuthenticationSuccess(req: HttpServletRequest, res: HttpServletResponse, auth: Authentication) {
            doBeforeRedirect(auth)
            super.onAuthenticationSuccess(req, res, auth)
        }
    }

    object SuccessSignInRedirectStrategy : DefaultRedirectStrategy() {
        override fun calculateRedirectUrl(contextPath: String?, url: String?): String {
            return super.calculateRedirectUrl(contextPath, url).let { path ->
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
    }

    companion object {
        private const val URL_PATH_ROOT = "/"
        private const val URL_PATH_SIGN_IN = "/sign-in"
        private const val URL_PATH_SIGN_OUT = "/sign-out"
        private const val PARAM_SUCCESS = "success"
    }
}
