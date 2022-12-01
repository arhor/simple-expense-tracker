package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import com.github.arhor.simple.expense.tracker.util.NULL_STRING
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/auth-providers")
class AuthProviderController(appProps: ApplicationProps, clientRegistrations: Iterable<ClientRegistration>) {

    private val availableProviders: List<AuthProviderDTO> =
        clientRegistrations.asSequence()
            .filter {
                (it.clientId != NULL_STRING && it.clientSecret != NULL_STRING).also { available ->
                    if (!available) {
                        logger.warn(
                            "OAuth2 provider '{}' is unavailable: missing client credentials",
                            it.registrationId
                        )
                    }
                }
            }
            .map {
                authProvider(
                    name = it.registrationId,
                    href = appProps.authRequestBaseUri + "/" + it.registrationId,
                )
            }
            .toList()

    @GetMapping
    fun getAuthProviders() = availableProviders

    companion object {
        private val logger = LoggerFactory.getLogger(AuthProviderController::class.java)

        @Suppress("NOTHING_TO_INLINE")
        private inline fun authProvider(name: String, href: String) = AuthProviderDTO(name, href)
    }
}
