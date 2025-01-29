package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO
import com.github.arhor.simple.expense.tracker.service.AuthProviderService
import com.github.arhor.simple.expense.tracker.service.util.NULL_STRING
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.stereotype.Service

@Service
class AuthProviderServiceImpl(
    appProps: ApplicationProps,
    registrations: Iterable<ClientRegistration>,
) : AuthProviderService {

    private val availableProviders: List<AuthProviderDTO>

    init {
        availableProviders = registrations
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
                    /* href = */ appProps.authRequestBaseUri + "/" + it.registrationId,
                )
            }
            .toList()
    }

    override fun getAvailableProviders(): List<AuthProviderDTO> {
        return availableProviders
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthProviderServiceImpl::class.java)
    }
}
