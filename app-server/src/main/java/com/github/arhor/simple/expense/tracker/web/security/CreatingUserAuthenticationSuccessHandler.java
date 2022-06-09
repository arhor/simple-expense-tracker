package com.github.arhor.simple.expense.tracker.web.security;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.service.UserService;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CreatingUserAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Authentication authentication
    ) throws ServletException, IOException {
        userService.createNewUserIfNecessary(authentication);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
