package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.Currency;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;
import com.github.arhor.simple.expense.tracker.service.UserService;
import com.github.arhor.simple.expense.tracker.service.mapper.InternalUserMapper;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final InternalUserRepository userRepository;
    private final InternalUserMapper userMapper;

    @Override
    public Mono<UserResponseDTO> createNewUser(final UserRequestDTO userDTO) {
        val username = userDTO.getUsername();

        return userRepository.existsByUsername(username).flatMap(exists -> {
            if (exists) {
                return Mono.error(
                    new EntityDuplicateException(
                        "InternalUser", "username=" + username
                    )
                );
            }

            val user = userMapper.mapToUser(userDTO);
            val createdUser = userRepository.save(user);

            return createdUser.map(userMapper::mapToResponse);
        });
    }

    @Override
    public Mono<Void> createNewUserIfNecessary(final Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            val extId = authenticationToken.getName();
            val extProvider = authenticationToken.getAuthorizedClientRegistrationId();

            if ((extId != null) && (extProvider != null)) {
                return userRepository.existsByExternalIdAndExternalProvider(extId, extProvider).flatMap(exists -> {
                    if (exists) {
                        return Mono.empty();
                    }
                    return userRepository.save(
                        InternalUser.builder()
                            .externalId(extId)
                            .externalProvider(extProvider)
                            .currency(Currency.USD.name())
                            .build()
                    ).then();
                });
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<UserResponseDTO> determineUser(final Authentication auth) {
        return determineInternalUser(auth).map(userMapper::mapToResponse);
    }

    private Mono<InternalUser.Projection> determineInternalUser(final Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken token) {
            val externalId = token.getName();
            val externalProvider = token.getAuthorizedClientRegistrationId();

            return userRepository.findByExternalIdAndProvider(externalId, externalProvider).switchIfEmpty(
                Mono.error(
                    () -> new EntityNotFoundException(
                        "User", "externalId=" + externalId + ", externalProvider=" + externalProvider
                    )
                )
            );
        }
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            val username = token.getName();

            return userRepository.findByUsername(username).switchIfEmpty(
                Mono.error(() ->
                    new EntityNotFoundException(
                        "User", "username=" + username
                    )
                )
            );
        }
        return Mono.error(() ->
            new IllegalArgumentException(
                "Unsupported authentication type: " + (
                    (auth != null)
                        ? auth.getClass().getSimpleName()
                        : "<null>"
                )
            )
        );
    }
}
