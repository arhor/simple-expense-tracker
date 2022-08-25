package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.model.projection.CompactInternalUserProjection
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.UserService
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: InternalUserRepository,
    private val userMapper: InternalUserMapper,
) : UserService {

    override fun determineUserId(auth: Authentication): Long? {
        val user = determineInternalUser(auth)
        return user.id
    }

    override fun determineUser(auth: Authentication): UserResponseDTO {
        val user = determineInternalUser(auth)
        return userMapper.mapToResponse(user)
    }

    @Transactional
    override fun createNewUser(request: UserRequestDTO): UserResponseDTO {
        val username = request.username

        if (userRepository.existsByUsername(username)) {
            throw EntityDuplicateException("InternalUser", "username=$username")
        }
        val user = userMapper.mapToUser(request)
        val createdUser = userRepository.save(user)
        return userMapper.mapToResponse(createdUser)
    }

    @Override
    override fun createNewUserIfNecessary(auth: Authentication) {
        if (auth is OAuth2AuthenticationToken) {
            val externalId = auth.name
            val externalProvider = auth.authorizedClientRegistrationId

            if (shouldCreateInternalUser(externalId, externalProvider)) {
                userRepository.save(
                    InternalUser(
                        externalId = externalId,
                        externalProvider = externalProvider,
                        currency = Currency.USD.name
                    )
                )
            }
        }
    }

    private fun shouldCreateInternalUser(externalId: String?, externalProvider: String?): Boolean {
        return (externalId != null)
            && (externalProvider != null)
            && !userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider)
    }

    private fun determineInternalUser(auth: Authentication): CompactInternalUserProjection {
        when (auth) {
            is OAuth2AuthenticationToken -> {
                val externalId = auth.name
                val externalProvider = auth.authorizedClientRegistrationId

                return userRepository.findByExternalIdAndProvider(externalId, externalProvider)
                    ?: throw EntityNotFoundException(
                        "User", "externalId=$externalId, externalProvider=$externalProvider"
                    )
            }

            is UsernamePasswordAuthenticationToken -> {
                val username = auth.name

                return userRepository.findByUsername(username)
                    ?: throw EntityNotFoundException("User", "username=$username")
            }

            else -> throw IllegalArgumentException(
                "Unsupported authentication type: ${auth::class.simpleName}"
            )
        }
    }
}
