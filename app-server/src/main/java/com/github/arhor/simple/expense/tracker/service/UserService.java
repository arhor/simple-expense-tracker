package com.github.arhor.simple.expense.tracker.service;

import org.springframework.security.core.Authentication;

import com.github.arhor.simple.expense.tracker.model.UserRequest;
import com.github.arhor.simple.expense.tracker.model.UserResponse;

public interface UserService {

    Long determineUserId(Authentication auth);

    UserResponse determineUser(Authentication auth);

    UserResponse createNewUser(UserRequest request);

    void createNewUserIfNecessary(Authentication authentication);
}
