package com.github.arhor.simple.expense.tracker.web.security;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.service.UserService;

import static reactor.core.publisher.Mono.defer;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CreatingUserAuthenticationSuccessHandler extends RedirectServerAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public Mono<Void> onAuthenticationSuccess(final WebFilterExchange exchange, final Authentication auth) {
        return userService
            .createNewUserIfNecessary(auth)
            .then(defer(() -> super.onAuthenticationSuccess(exchange, auth)));
    }
}
