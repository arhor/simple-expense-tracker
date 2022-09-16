package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val applicationProps: ApplicationProps,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .logout()
            .logoutUrl(URL_PATH_SIGN_OUT.withApiPrefix())
            .logoutSuccessUrl(URL_PATH_ROOT)
            .and()
            .formLogin()
            .loginPage(URL_PATH_SIGN_IN)
            .loginProcessingUrl(URL_PATH_SIGN_IN.withApiPrefix())
            .successHandler(authenticationSuccessHandler)
            .and()
            .oauth2Login()
            .loginPage(URL_PATH_SIGN_IN)
            .authorizationEndpoint()
            .baseUri(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI.withApiPrefix())
            .and()
            .successHandler(authenticationSuccessHandler)
            .and()
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("script-src 'self' 'unsafe-inline'")
    }

    private fun String.withApiPrefix() = applicationProps.apiUrlPath(this)

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
