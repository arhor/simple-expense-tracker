package com.github.arhor.simple.expense.tracker.service;

import reactor.core.publisher.Mono;

import org.springframework.security.core.Authentication;

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;

public interface UserService {

    Mono<UserResponseDTO> createNewUser(UserRequestDTO userDTO);

    Mono<UserResponseDTO> determineUser(Authentication auth);

    Mono<Void> createNewUserIfNecessary(Authentication auth);
}
