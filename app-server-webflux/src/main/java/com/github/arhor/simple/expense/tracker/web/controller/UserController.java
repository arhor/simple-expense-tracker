package com.github.arhor.simple.expense.tracker.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;
import com.github.arhor.simple.expense.tracker.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<UserResponseDTO>> createUser(
        @Valid @RequestBody final UserRequestDTO userDTO,
        final UriComponentsBuilder uriBuilder,
        final ServerHttpRequest request
    ) {
        return userService.createNewUser(userDTO).map(createdUser -> {

            val location =
                uriBuilder.uri(request.getURI())
                    .path("/")
                    .queryParam("current")
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(createdUser);
        });
    }

    @GetMapping(params = "current")
    @PreAuthorize("isAuthenticated()")
    public Mono<UserResponseDTO> getCurrentUser(final Authentication auth) {
        return userService.determineUser(auth);
    }
}
