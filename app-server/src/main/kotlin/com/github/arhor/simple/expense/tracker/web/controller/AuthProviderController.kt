package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import com.github.arhor.simple.expense.tracker.util.NULL_STRING
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth-providers")
class AuthProviderController(appProps: ApplicationProps, clientRegistrations: Iterable<ClientRegistration>) {

    private val availableProviders = collectProviders(appProps.authRequestBaseUri, clientRegistrations)

    @GetMapping
    fun getAuthProviders() = availableProviders

    private fun collectProviders(baseUri: String, registrations: Iterable<ClientRegistration>): List<AuthProviderDTO> =
        registrations
            .asSequence()
            .filter {
                (it.clientId != NULL_STRING && it.clientSecret != NULL_STRING).also { available ->
                    if (!available) {
                        logger.warn(
                            "Auth provider '{}' is unavailable: missing client credentials",
                            it.registrationId
                        )
                    }
                }
            }
            .map {
                AuthProviderDTO(
                    /* name = */ it.registrationId,
                    /* href = */ baseUri + "/" + it.registrationId,
                )
            }
            .toList()

    companion object {
        private val logger = LoggerFactory.getLogger(AuthProviderController::class.java)
    }
}
