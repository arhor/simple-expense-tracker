package com.github.arhor.simple.expense.tracker.web.security;

import org.springframework.security.core.Authentication;

public interface UserAttributeResolver {

    String resolveAttribute(Authentication auth, String name);
}
