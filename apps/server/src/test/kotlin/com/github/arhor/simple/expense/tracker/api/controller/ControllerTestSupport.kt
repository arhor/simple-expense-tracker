package com.github.arhor.simple.expense.tracker.api.controller

import com.github.arhor.simple.expense.tracker.config.ConfigureLocalization
import com.github.arhor.simple.expense.tracker.config.ConfigureWebSecurity
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.CustomUserDetails
import com.github.arhor.simple.expense.tracker.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@Tag("contract")
@Import(
    value = [
        ConfigureWebSecurity::class,
        ConfigureLocalization::class,
    ]
)
@MockkBean(
    value = [
        UserService::class,
    ]
)
@ExtendWith(
    value = [
        SpringExtension::class,
    ]
)
@EnableConfigurationProperties(
    value = [
        ApplicationProps::class,
        OAuth2ClientProperties::class,
    ]
)
internal abstract class ControllerTestSupport {

    @Autowired
    protected lateinit var http: MockMvc
        private set

    @Autowired
    protected lateinit var appProps: ApplicationProps
        private set

    @BeforeEach
    fun setUp(@Autowired userService: UserService) {
        every { userService.loadUserByUsername(any()) } returns TestUser
    }
}

internal object TestUser : CustomUserDetails(
    id = 1L,
    currency = "USD",
    username = "TestUser",
    password = "StrongPassword123",
    authorities = listOf(SimpleGrantedAuthority("ROLE_USER")),
)
