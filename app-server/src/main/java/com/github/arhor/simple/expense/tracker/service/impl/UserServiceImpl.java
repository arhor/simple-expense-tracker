package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.arhor.simple.expense.tracker.data.model.InternalUser;
import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;
import com.github.arhor.simple.expense.tracker.exception.EntityDuplicateException;
import com.github.arhor.simple.expense.tracker.exception.EntityNotFoundException;
import com.github.arhor.simple.expense.tracker.model.Currency;
import com.github.arhor.simple.expense.tracker.model.UserRequestDTO;
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO;
import com.github.arhor.simple.expense.tracker.service.UserService;
import com.github.arhor.simple.expense.tracker.service.mapping.InternalUserMapper;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final InternalUserRepository userRepository;
    private final InternalUserMapper userMapper;

    @Override
    public Long determineUserId(final Authentication auth) {
        val user = determineInternalUser(auth);
        return user.id();
    }

    @Override
    public UserResponseDTO determineUser(Authentication auth) {
        val user = determineInternalUser(auth);
        return userMapper.mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createNewUser(final UserRequestDTO request) {
        val username = request.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new EntityDuplicateException("InternalUser", "username=" + username);
        }
        val user = userMapper.mapToUser(request);
        val createdUser = userRepository.save(
            user.currency() == null
                ? user.toBuilder().currency("USD").build()
                : user
        );
        return userMapper.mapToResponse(createdUser);
    }

    @Override
    public void createNewUserIfNecessary(final Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            val externalId = authenticationToken.getName();
            val externalProvider = authenticationToken.getAuthorizedClientRegistrationId();

            if (shouldCreateInternalUser(externalId, externalProvider)) {
                userRepository.save(
                    InternalUser.builder()
                        .externalId(externalId)
                        .externalProvider(externalProvider)
                        .currency(Currency.USD.name())
                        .build()
                );
            }
        }
    }

    private boolean shouldCreateInternalUser(final String externalId, final String externalProvider) {
        return (externalId!=null)
            && (externalProvider!=null)
            && !userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);
    }

    private InternalUser.Projection determineInternalUser(final Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken token) {
            val externalId = token.getName();
            val externalProvider = token.getAuthorizedClientRegistrationId();

            return userRepository.findByExternalIdAndProvider(externalId, externalProvider).orElseThrow(() ->
                new EntityNotFoundException(
                    "User", "externalId=" + externalId + ", externalProvider=" + externalProvider
                )
            );
        }
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            val username = token.getName();

            return userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException(
                    "User", "username=" + username
                )
            );
        }
        throw new IllegalArgumentException(
            "Unsupported authentication type: " + (
                (auth!=null)
                    ? auth.getClass().getSimpleName()
                    :"<null>"
            )
        );
    }
}
