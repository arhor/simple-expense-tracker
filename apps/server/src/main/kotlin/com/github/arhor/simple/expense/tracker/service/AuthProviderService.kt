package com.github.arhor.simple.expense.tracker.service

import com.github.arhor.simple.expense.tracker.model.AuthProviderDTO

interface AuthProviderService {

    fun getAvailableProviders(): List<AuthProviderDTO>
}
