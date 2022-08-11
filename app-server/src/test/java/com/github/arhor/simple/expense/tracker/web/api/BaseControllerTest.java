package com.github.arhor.simple.expense.tracker.web.api;

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

import com.github.arhor.simple.expense.tracker.ContractTest;
import com.github.arhor.simple.expense.tracker.config.LocalizationConfig;
import com.github.arhor.simple.expense.tracker.config.properties.ApplicationProps;
import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl;

@ContractTest
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

    boolean authenticatedUser(final Authentication auth) {
        return auth.isAuthenticated() && "user".equals(auth.getName());
    }
}
