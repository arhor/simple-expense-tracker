package com.github.arhor.simple.expense.tracker.config

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.session.MapSession
import org.springframework.session.MapSessionRepository
import org.springframework.session.SessionRepository
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val applicationProps: ApplicationProps,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
) : WebSecurityConfigurerAdapter() {

    public override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .logout()
            .logoutUrl(applicationProps!!.apiUrlPath("/logout"))
            .logoutSuccessUrl("/")
            .and()
            .formLogin()
            .loginProcessingUrl(applicationProps.apiUrlPath("/login"))
            .successHandler(authenticationSuccessHandler)
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri(applicationProps.apiUrlPath(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI))
            .and()
            .successHandler(authenticationSuccessHandler)
            .and()
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("script-src 'self' 'unsafe-inline'")
    }

    @Configuration(proxyBeanMethods = false)
    class Beans {
        @Bean
        fun sessionRepository(): SessionRepository<MapSession> {
            return MapSessionRepository(ConcurrentHashMap())
        }

        @Bean
        fun passwordEncoder(): PasswordEncoder {
            return BCryptPasswordEncoder()
        }
    }
}