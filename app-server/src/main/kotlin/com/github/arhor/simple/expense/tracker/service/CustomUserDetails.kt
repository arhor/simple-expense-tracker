package com.github.arhor.simple.expense.tracker.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

open class CustomUserDetails(
    val id: Long,
    val currency: String,
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>,
) : User(username, password, authorities)
