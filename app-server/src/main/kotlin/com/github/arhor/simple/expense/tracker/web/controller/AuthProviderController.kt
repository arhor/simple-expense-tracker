package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.service.AuthProviderService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth-providers")
class AuthProviderController(private val authProviderService: AuthProviderService) {

    @Operation(
        summary = "Available OAuth2 providers",
        description = "Returns the list of the OAuth2 providers available to use",
    )
    @GetMapping
    fun getAuthProviders() = authProviderService.getAvailableProviders()
}
