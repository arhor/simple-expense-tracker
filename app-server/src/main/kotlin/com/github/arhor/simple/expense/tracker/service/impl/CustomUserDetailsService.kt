package com.github.arhor.simple.expense.tracker.service.impl;

import com.github.arhor.simple.expense.tracker.data.repository.InternalUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: InternalUserRepository) : UserDetailsService {

    @Override
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findInternalUserByUsername(username)?.let {
            User.builder()
                .username(it.username)
                .password(it.password)
                .roles("USER")
                .build()
        } ?: throw UsernameNotFoundException("InternalUser with username '$username' is not found.")
    }
}
