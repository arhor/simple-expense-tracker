package com.github.arhor.simple.expense.tracker.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.github.arhor.simple.expense.tracker.service.UserService;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreatingUserAuthenticationSuccessHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CreatingUserAuthenticationSuccessHandler authenticationSuccessHandler;

    @Test
    void should_call_createNewUserIfNecessary_on_UserService_on_authentication_success()
        throws ServletException, IOException {
        // when
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        then(userService)
            .should()
            .createNewUserIfNecessary(authentication);
    }
}
