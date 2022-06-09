package com.github.arhor.simple.expense.tracker.web.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class UserAttributeResolverImpl implements UserAttributeResolver {

    @Override
    public String resolveAttribute(final Authentication auth, final String name) {
        if (auth instanceof OAuth2AuthenticationToken oauth2Token) {
            var principal = oauth2Token.getPrincipal();
            var email = principal.getAttribute(name);
            if (email == null) {
                throw new IllegalStateException(
                    "Principal attribute '%s' cannot be null".formatted(
                        name
                    )
                );
            }
            return email.toString();
        }
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            // TODO: implement handling for the form login
            return token.getName();
        }
        throw new IllegalArgumentException(
            "Illegal authentication type: %s".formatted(
                (auth != null)
                    ? auth.getClass().getSimpleName()
                    : null
            )
        );
    }
}
