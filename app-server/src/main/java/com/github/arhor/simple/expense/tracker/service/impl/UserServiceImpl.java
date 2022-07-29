package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class UserServiceImpl implements UserService, UserDetailsService {

    private final InternalUserRepository userRepository;
    private final InternalUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findInternalUserByUsername(username)
            .map(internalUser ->
                User.builder()
                    .username(internalUser.username())
                    .password(internalUser.password())
                    .authorities("ROLE_USER")
                    .build()
            )
            .orElseThrow(() ->
                new UsernameNotFoundException(
                    "InternalUser with username '" + username + "' is not found."
                )
            );
    }

    @Override
    public Long determineUserId(final Authentication auth) {
        var user = determineInternalUser(auth);
        return user.id();
    }

    @Override
    public UserResponseDTO determineUser(Authentication auth) {
        var user = determineInternalUser(auth);
        return userMapper.mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO createNewUser(final UserRequestDTO request) {
        var username = request.getUsername();

        if (userRepository.existsByUsername(username)) {
            throw new EntityDuplicateException("InternalUser", "username=" + username);
        }

        var user = userMapper.mapToUser(request);
        var createdUser = userRepository.save(user);

        return userMapper.mapToResponse(createdUser);
    }

    @Override
    public void createNewUserIfNecessary(final Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            var externalId = authenticationToken.getName();
            var externalProvider = authenticationToken.getAuthorizedClientRegistrationId();

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
        return (externalId != null)
            && (externalProvider != null)
            && !userRepository.existsByExternalIdAndExternalProvider(externalId, externalProvider);
    }

    private InternalUser.Projection determineInternalUser(final Authentication auth) {
        return switch (auth) {
            case final OAuth2AuthenticationToken token -> {
                var externalId = token.getName();
                var externalProvider = token.getAuthorizedClientRegistrationId();

                yield userRepository.findByExternalIdAndProvider(externalId, externalProvider).orElseThrow(() ->
                    new EntityNotFoundException(
                        "User", "externalId=" + externalId + ", externalProvider=" + externalProvider
                    )
                );
            }
            case final UsernamePasswordAuthenticationToken token -> {
                var username = token.getName();

                yield userRepository.findByUsername(username).orElseThrow(() ->
                    new EntityNotFoundException(
                        "User", "username=" + username
                    )
                );
            }
            case null, default -> throw new IllegalArgumentException(
                "Unsupported authentication type: " + (
                    (auth != null)
                        ? auth.getClass().getSimpleName()
                        : "<null>"
                )
            );
        };
    }
}
