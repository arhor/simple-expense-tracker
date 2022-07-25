package com.github.arhor.simple.expense.tracker.service;

import org.springframework.security.core.Authentication;

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;

public interface UserService {

    Long determineUserId(Authentication auth);

    UserResponseDTO determineUser(Authentication auth);

    UserResponseDTO createNewUser(UserRequestDTO request);

    void createNewUserIfNecessary(Authentication authentication);
}
