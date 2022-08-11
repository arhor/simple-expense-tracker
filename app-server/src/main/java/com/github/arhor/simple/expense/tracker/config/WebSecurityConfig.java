package com.github.arhor.simple.expense.tracker.config;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${configuration.api-path-prefix:/api}")
    private final String apiPathPrefix;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    // TODO: is it necessary?
    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .logout()
            .logoutUrl(apiUrlPath("/logout"))
            .logoutSuccessUrl("/")
            .and()
            .formLogin()
            .loginProcessingUrl(apiUrlPath("/login"))
            .successHandler(authenticationSuccessHandler)
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri(apiUrlPath(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI))
            .and()
            .successHandler(authenticationSuccessHandler)
            .and()
            .headers()
            .xssProtection()
            .and()
            .contentSecurityPolicy("script-src 'self' 'unsafe-inline'");
    }

    private String apiUrlPath(final String url) {
        return apiPathPrefix + url;
    }

    @Configuration(proxyBeanMethods = false)
    public static class Beans {

        @Bean
        public SessionRepository<MapSession> sessionRepository() {
            return new MapSessionRepository(new ConcurrentHashMap<>());
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
