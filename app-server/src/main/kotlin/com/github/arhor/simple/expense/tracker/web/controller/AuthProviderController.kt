package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Collections

@RestController
@RequestMapping("/auth-providers")
class AuthProviderController(appProps: ApplicationProps, clientRegistrations: Iterable<ClientRegistration>) {

    private val availableProviders: List<AuthProviderDTO> = appProps.authorizationEndpointBaseUri.let { baseUri ->
        Collections.unmodifiableList(
            clientRegistrations.map {
                AuthProviderDTO(
                    /* name = */ it.registrationId,
                    /* href = */ "${baseUri}/${it.registrationId}",
                )
            }
        )
    }

    @GetMapping
    fun getAuthProviders() = availableProviders
}
