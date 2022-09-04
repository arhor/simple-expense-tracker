package com.github.arhor.simple.expense.tracker.web.controller

import com.github.arhor.simple.expense.tracker.config.LocalizationConfig
import com.github.arhor.simple.expense.tracker.config.props.ApplicationProps
import com.github.arhor.simple.expense.tracker.service.impl.TimeServiceImpl
import com.ninjasquad.springmockk.MockkBean
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.test.web.servlet.MockMvc

@Tag("contract")
@EnableConfigurationProperties(ApplicationProps::class)
@Import(LocalizationConfig::class, TimeServiceImpl::class)
internal abstract class BaseControllerTest {

    @Autowired
    protected lateinit var http: MockMvc

    @MockkBean
    protected lateinit var authenticationSuccessHandler: AuthenticationSuccessHandler

    @MockkBean
    protected lateinit var userDetailsService: UserDetailsService

    @MockkBean
    protected lateinit var passwordEncoder: PasswordEncoder

    @MockkBean
    protected lateinit var clientRegistrationRepository: ClientRegistrationRepository

    fun authenticatedUser(auth: Authentication) {
        assertSoftly { soft ->
            soft.assertThat(auth.isAuthenticated)
                .describedAs("authenticated")
                .isTrue
            soft.assertThat(auth.name)
                .describedAs("authentication name")
                .isEqualTo("user")
        }
    }
}
