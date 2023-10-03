package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {

    fun getUserById(userId: Long): UserResponseDTO

    fun createNewUser(request: UserRequestDTO): UserResponseDTO

    fun createInternalUserIfNecessary(auth: Authentication)
}
