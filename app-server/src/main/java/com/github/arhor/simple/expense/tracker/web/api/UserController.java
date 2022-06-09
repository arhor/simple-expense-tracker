package com.github.arhor.simple.expense.tracker.web.api;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.arhor.simple.expense.tracker.model.UserRequest;
import com.github.arhor.simple.expense.tracker.model.UserResponse;
import com.github.arhor.simple.expense.tracker.service.UserService;
import com.github.arhor.simple.expense.tracker.web.security.UserAttributeResolver;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserAttributeResolver userAttributeResolver;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody final UserRequest request) {
        var createdUser = userService.createNewUser(request);

        var location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/current")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public UserResponse getCurrentUser(final Authentication auth) {
        return userService.determineUser(auth);
    }
}
