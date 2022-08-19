package com.github.arhor.simple.expense.tracker.web.controller;

import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;

import com.github.arhor.simple.expense.tracker.config.LocalizationConfig;
import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;
import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Tag("contract")
@EnableConfigurationProperties({ApplicationProps.class})
@Import({LocalizationConfig.class, TimeServiceImpl.class})
abstract class BaseControllerTest {

    @Autowired
    protected MockMvc http;

    @MockBean
    protected AuthenticationSuccessHandler authenticationSuccessHandler;
    @MockBean
    protected UserDetailsService userDetailsService;
    @MockBean
    protected PasswordEncoder passwordEncoder;
    @MockBean
    protected ClientRegistrationRepository clientRegistrationRepository;

    void authenticatedUser(final Authentication auth) {
        assertSoftly(soft -> {
            soft.assertThat(auth.isAuthenticated())
                .as("authenticated")
                .isTrue();
            soft.assertThat(auth.getName())
                .as("authentication name")
                .isEqualTo("user");
        });
    }
}
