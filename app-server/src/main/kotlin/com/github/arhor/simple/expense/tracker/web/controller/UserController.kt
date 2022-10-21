package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val createdUser = userService.createNewUser(request)

        val location =
            ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/")
                .queryParam("current")
                .build()
                .toUri()

        return ResponseEntity.created(location).body(createdUser)
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(auth: Authentication): UserResponseDTO {
        return userService.determineUser(auth)
    }
}
