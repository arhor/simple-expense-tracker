package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.UserRequestDTO
import com.github.arhor.simple.expense.tracker.model.UserResponseDTO
import org.springframework.security.core.Authentication

interface UserService {

    fun determineUserId(auth: Authentication): Long

    fun getUserById(userId: Long): UserResponseDTO

    fun determineUser(auth: Authentication): UserResponseDTO

    fun createNewUser(request: UserRequestDTO): UserResponseDTO

    fun createNewUserIfNecessary(auth: Authentication)
}
