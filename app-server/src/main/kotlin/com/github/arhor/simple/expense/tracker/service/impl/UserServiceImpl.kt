package com.github.arhor.simple.expense.tracker.service.impl

import com.github.arhor.simple.expense.tracker.data.model.InternalUser
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException
import com.github.arhor.simple.expense.tracker.model.Currency
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.UserService
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: InternalUserRepository,
    private val userMapper: InternalUserMapper,
) : UserService {

    override fun loadUserByUsername(username: String): CustomUserDetails {
        return userRepository.findInternalUserByUsername(username)?.let(::convertInternalUserToUserDetails)
            ?: throw UsernameNotFoundException("InternalUser with username '$username' is not found.")
    }

    override fun getUserById(userId: Long): UserResponseDTO {
        return userRepository.findByIdOrNull(userId)?.let(userMapper::mapToResponse)
            ?: throw EntityNotFoundException(InternalUser.ENTITY_NAME, "id=${userId}")
    }

    @Transactional
    override fun createNewUser(request: UserRequestDTO): UserResponseDTO {
        val username = request.username

        if (userRepository.existsByUsername(username)) {
            throw EntityDuplicateException(InternalUser.ENTITY_NAME, "username=$username")
        }
        val user = userMapper.mapToUser(request)
        val createdUser = userRepository.save(user)
        return userMapper.mapToResponse(createdUser)
    }

    @Override
    override fun createInternalUserIfNecessary(auth: Authentication) {
        if (auth is OAuth2AuthenticationToken) {
            val externalId = auth.name
            val externalProvider = auth.authorizedClientRegistrationId

            val shouldCreateInternalUser =
                !userRepository.existsByExternalIdAndExternalProvider(
                    externalId,
                    externalProvider
                )

            if (shouldCreateInternalUser) {
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

    // delegate to mapper
    private fun convertInternalUserToUserDetails(it: InternalUser) = CustomUserDetails(
        id = it.id ?: throw IllegalStateException("id cannot be null"),
        currency = it.currency,
        username = it.username ?: throw IllegalStateException("username cannot be null"),
        password = it.password ?: throw IllegalStateException("password cannot be null"),
        authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
    )
}
