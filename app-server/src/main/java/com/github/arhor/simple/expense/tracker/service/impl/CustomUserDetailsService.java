package com.github.arhor.simple.expense.tracker.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CustomUserDetailsService implements UserDetailsService {

    private final InternalUserRepository userRepository;

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
}
