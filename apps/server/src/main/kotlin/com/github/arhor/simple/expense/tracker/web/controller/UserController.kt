package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @Operation(
        summary = "Creates new user",
        description = "Creates new user according to provided request body",
    )
    @PostMapping
    fun createUser(@Valid @RequestBody requestBody: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val createdUser = userService.createNewUser(requestBody)
        val locationUri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{userId}").build(createdUser.id)

        return ResponseEntity.created(locationUri).body(createdUser)
    }

    @Operation(
        summary = "User information by id",
        description = "Returns information for the user by its id",
        security = [
            SecurityRequirement(name = "authenticated")
        ]
    )
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    fun getUserById(@PathVariable userId: Long): UserResponseDTO {
        return userService.getUserById(userId)
    }

    @Operation(
        summary = "Current user information",
        description = "Returns currently authenticated user information",
        security = [
            SecurityRequirement(name = "authenticated")
        ]
    )
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(@AuthenticationPrincipal currentUser: CustomUserDetails) = UserResponseDTO(
        /* id       = */ currentUser.id,
        /* username = */ currentUser.username,
        /* currency = */ currentUser.currency,
    )
}
