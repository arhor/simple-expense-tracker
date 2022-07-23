package com.github.arhor.simple.expense.tracker;

import org.springframework.security.core.Authentication;

public final class CustomArgumentMatchers {

    public static boolean authenticatedUser(final Authentication auth) {
        return auth.isAuthenticated() && "user".equals(auth.getName());
    }

    private CustomArgumentMatchers() { /* should not be instantiated */ }
}
