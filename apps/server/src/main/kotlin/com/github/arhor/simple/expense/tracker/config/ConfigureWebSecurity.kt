package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.config.security.CustomFailureHandler
import com.github.arhor.simple.expense.tracker.config.security.CustomSuccessHandler
import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler

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
                it.failureHandler(CustomFailureHandler())
            }
            .oauth2Login {
                it.loginPage(URL_PATH_SIGN_IN)
                it.authorizationEndpoint().baseUri(appProps.authRequestBaseUri)
                it.successHandler(CustomSuccessHandler(doBeforeRedirect = userService::createInternalUserIfNecessary))
            }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    companion object {
        private const val URL_PATH_ROOT = "/"
        private const val URL_PATH_SIGN_IN = "/sign-in"
        private const val URL_PATH_SIGN_OUT = "/sign-out"
    }
}
